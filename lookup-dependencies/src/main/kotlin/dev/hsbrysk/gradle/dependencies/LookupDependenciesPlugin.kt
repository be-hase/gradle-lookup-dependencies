package dev.hsbrysk.gradle.dependencies

import org.gradle.api.Plugin
import org.gradle.api.Project

class LookupDependenciesPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register a task
        project.tasks.register("lookupDependencies", LookupDependenciesTask::class.java)
    }
}
