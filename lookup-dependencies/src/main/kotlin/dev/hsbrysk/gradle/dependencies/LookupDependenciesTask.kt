package dev.hsbrysk.gradle.dependencies

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.internal.logging.text.StyledTextOutput.Style
import org.gradle.internal.logging.text.StyledTextOutputFactory
import java.lang.reflect.Method
import javax.inject.Inject

open class LookupDependenciesTask @Inject constructor(
    outputFactory: StyledTextOutputFactory,
) : DefaultTask() {
    @get:Input
    @set:Option(option = "dependency", description = "The dependency you want to lookup")
    var dependency: String = ""

    @get:Input
    @set:Option(
        option = "configuration",
        description = "The configuration you want to lookup. Default is all configuration.",
    )
    var configuration: String = ""

    @Suppress("ktlint:standard:max-line-length")
    @get:Input
    @set:Option(
        option = "allow-revisit",
        description = "When the dependency tree is being explored, omit this if the dependency has already been explored.",
    )
    var allowRevisit: Boolean = false

    @get:Input
    @set:Option(
        option = "version-range",
        description = "Comparative expression of version to lookup. e.g. `>= 1.3, < 1.26.0`",
    )
    var versionRange: String = ""

    private val out = outputFactory.create("example-task")

    @TaskAction
    fun run() {
        val target = TargetDependency.of(dependency, versionRange)

        val configurations = project.configurations
            .filter { configuration.isEmpty() || it.name == configuration }
            .filter { canSafelyBeResolved(it) }.distinct()

        val allResult = configurations
            .map { configuration ->
                val root = configuration.incoming.resolutionResult.rootComponent.get()
                configuration.name to lookup(root, target)
            }
            .filter { it.second.isNotEmpty() }

        if (allResult.isNotEmpty()) {
            out.style(Style.Header).println(
                """

                ------------------------------------------------------------
                Project ${project.path}
                ------------------------------------------------------------

                """.trimIndent(),
            )
            allResult.forEach { pair ->
                val (configurationName, result) = pair
                if (result.isNotEmpty()) {
                    printConfigurationResult(configurationName, result)
                }
            }
        }
    }

    private fun printConfigurationResult(
        configurationName: String,
        configurationResult: List<List<ResolvedDependencyResult>>,
    ) {
        out.style(Style.SuccessHeader).println("< $configurationName >")
        configurationResult.forEach { dependenciesTree ->
            dependenciesTree.forEachIndexed { i, dependency ->
                val indent = " ".repeat(i * 4)
                if (i < dependenciesTree.size - 1) {
                    println("$indent${dependency.display()}")
                } else {
                    out.style(Style.Info).println("$indent${dependency.display()}")
                }
            }
        }
        println()
    }

    private fun lookup(
        root: ResolvedComponentResult,
        target: TargetDependency,
    ): List<List<ResolvedDependencyResult>> {
        val result = mutableListOf<List<ResolvedDependencyResult>>()
        val processed = mutableSetOf<DependencyIdentifier>()

        root.getResolvedDependencies().forEach { child ->
            lookupRecursively(
                child,
                listOf(),
                target,
                result,
                processed,
            )
        }

        return result
    }

    private fun lookupRecursively(
        current: ResolvedDependencyResult,
        parents: List<ResolvedDependencyResult>,
        target: TargetDependency,
        result: MutableList<List<ResolvedDependencyResult>>,
        processed: MutableSet<DependencyIdentifier>,
    ) {
        // End when found.
        if (target.matches(current.selected.moduleVersion)) {
            result.add(parents + current)
            return
        }

        // Constraint is not a dependency and will not be explored further.
        if (current.isConstraint) {
            return
        }

        if (!allowRevisit) {
            val identifier = current.selected.moduleVersion.let {
                checkNotNull(it) // Here, this is always non-null
                DependencyIdentifier(it.group, it.name, it.version)
            }
            // If the dependency has already been explored, no further exploration is performed.
            if (!processed.add(identifier)) {
                return
            }
        }
        current.selected.getResolvedDependencies().forEach {
            lookupRecursively(it, parents + current, target, result, processed)
        }
    }

    companion object {
        private val canSafelyBeResolvedMethod: Method? = getCanSafelyBeResolvedMethod()

        /**
         * If `DeprecatableConfiguration.canSafelyBeResolve()` is available, use it.
         * Else fall back to `Configuration.canBeResolved`.
         */
        private fun canSafelyBeResolved(configuration: Configuration): Boolean {
            if (canSafelyBeResolvedMethod != null) {
                return canSafelyBeResolvedMethod.invoke(configuration) as Boolean
            }
            return configuration.isCanBeResolved
        }

        private fun getCanSafelyBeResolvedMethod(): Method? {
            return try {
                val dc = Class.forName("org.gradle.internal.deprecation.DeprecatableConfiguration")
                dc.getMethod("canSafelyBeResolved")
            } catch (expected: ReflectiveOperationException) {
                null
            }
        }

        private fun ResolvedComponentResult.getResolvedDependencies() =
            this.dependencies.mapNotNull { it as? ResolvedDependencyResult }

        private fun ResolvedDependencyResult.display(): String {
            val text = if (requested.matchesStrictly(selected.id)) {
                requested.toString()
            } else {
                val id = selected.id
                if (id is ModuleComponentIdentifier) {
                    "$requested -> ${id.version}"
                } else {
                    "$requested -> $id"
                }
            }

            return "$text${if (isConstraint) " (constraint)" else ""}"
        }
    }
}
