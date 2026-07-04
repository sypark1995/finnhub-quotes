pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "finnhub"
include(":app")
include(":core:common")
include(":core:domain")
include(":core:ui")
include(":core:network")
include(":core:websocket")
include(":core:database")
include(":core:datastore")
include(":core:data")
include(":feature:watchlist")
include(":feature:search")
include(":feature:detail")
include(":feature:alert")
