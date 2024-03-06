pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://artifactory.paytm.in/libs-release-local")
        }
        mavenCentral()
    }
}

rootProject.name = "BollyHood"
include(":app")
 