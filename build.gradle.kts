plugins {
    kotlin("jvm") version "2.0.21" // Replace with your actual Kotlin version
}

group = "me.bananababoo"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://jitpack.io"){
        name = "jitpack"
    }
}

dependencies {
    compileOnly(libs.paper.api)
    implementation(libs.kotlin.stdlib) // https://modrinth.com/plugin/ktlibs-kotlin-stdlib
    implementation(libs.kotlin.script.runtime)
    testImplementation(libs.mockbukkit)
    testImplementation(libs.mockk)
    testImplementation(libs.logback)
    testImplementation(kotlin("test"))
}

val targetJavaVersion = 21

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.test {
    jvmArgs("-Xshare:off","-XX:+EnableDynamicAgentLoading", "-Djdk.instrument.traceUsage") // gets rid of warning
    useJUnitPlatform()
}

tasks.jar {
    destinationDirectory = file(providers.gradleProperty("build.outputPath"))
}