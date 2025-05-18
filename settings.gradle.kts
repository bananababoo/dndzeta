pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.0.21"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "dndzeta"

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/groups/public/") {
            name = "sonatype"
        }
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "papermc-repo"
        }
        maven("https://www.jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.xenondevs.xyz/releases")
    }
}