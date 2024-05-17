# gradle-lookup-dependencies

## Motivation

There are cases where you want to check whether a specific dependency is included, and if it is, where it came from
(e.g., when vulnerabilities are reported via tools like Dependabot).

You can investigate using tasks like ./gradlew dependencies or ./gradlew dependencyReport,
but it requires using tools like grep and can be cumbersome.

By using this plugin, you can solve these issues easily.

## How to use?

### TL;DR

```
./gradlew lookupDependencies --artifact org.springframework:spring-web --version-expressions ">= 6.1.0, < 6.1.6"

...

< testImplementationDependenciesMetadata >
org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0
    org.springdoc:springdoc-openapi-starter-webmvc-api:2.5.0
        org.springframework:spring-webmvc:6.1.5
            org.springframework:spring-web:6.1.5

...
```

### Task Options

```shell
./gradlew help --task lookupDependencies

Options
     --allow-revisit     Allows the traversal to visit dependency multiple times, including those that have been visited before. Default is false

     --no-allow-revisit     Disables option --allow-revisit.

     --artifact     The artifact you want to lookup

     --configuration     The configuration you want to lookup. Default is all configuration.

     --version-expressions     Comparative expression of version to lookup. e.g. `>= 1.3, < 1.26.0`
```

### Examples

```shell
# lookup org.springframework:spring-web with version is 6.1.6
./gradlew lookupDependencies --dependency org.springframework:spring-web:6.1.6

# lookup org.springframework:spring-web (version is not considered)
./gradlew lookupDependencies --artifact org.springframework:spring-web:6.1.6

# lookup org.springframework:spring-web with version in the range `>= 6.1.0` and `< 6.1.6`
./gradlew lookupDependencies --artifact org.springframework:spring-web:6.1.6
```

## Contributing

If there are any issues, please feel free to send a pull request.
