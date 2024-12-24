plugins {
    kotlin("jvm") version "2.1.0-Beta2" // Replace with your actual Kotlin version
    id("com.gradleup.shadow") version "8.3.5"
    id("com.github.gmazzo.buildconfig") version "5.5.0"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("nl.littlerobots.version-catalog-update") version "0.8.5"

}

group = "me.bananababoo"
version = "0.0.1"

dependencies {
    //paper
    compileOnly(libs.paper.api)
    //minecraft-adjacent
    implementation(libs.acf.paper)
    implementation(libs.adventure.extra.kotlin)
    //kotlin
    implementation(libs.kotlin.stdlib) // https://modrinth.com/plugin/ktlibs-kotlin-stdlib
    implementation(libs.kotlin.script.runtime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.reflect)
    implementation(libs.classgraph)
    //serialization
    implementation(libs.guava)
    implementation(libs.mongo.bson)
    implementation(libs.jackson)
    //database
    implementation(libs.mongo.jackdriver)
    implementation(libs.kmongo)
    //test
    testImplementation(libs.mockbukkit)
    testImplementation(libs.mockk)
    testImplementation(libs.logback)
    //gui library
    implementation(libs.invui)
    implementation(libs.invui.kotlin)
    //resource pack
    implementation(libs.creative.api)
    implementation(libs.creative.serializer)
    //google drive stuff
    implementation(libs.google.api.client)
    implementation(libs.google.api.client.jackson)
    implementation(libs.google.api.oauth.client.jetty)
    implementation(libs.google.drive)
    implementation(libs.google.auth)

    testImplementation(kotlin("test"))
}


val targetJavaVersion = 21

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
}

kotlin {
    compilerOptions {
        javaParameters = true
    }
}


tasks.compileJava {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
    options.compilerArgs.add("-parameters")
    options.isFork = true
    options.forkOptions.executable = "${System.getProperty("java.home")}/bin/javac.exe"
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

tasks.shadowJar {
    destinationDirectory = file(providers.gradleProperty("build.outputPath"))
}

tasks.shadowJar {
    relocate("co.aikar.commands", "org.banana_inc.acf")
    relocate("co.aikar.locales", "org.banana_inc.locales")
    relocate("ink.pmc.advkt", "com.example.libs.advkt")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

buildConfig {
    buildConfigField("databaseUsername", providers.gradleProperty("database.username"))
    buildConfigField("databasePassword", providers.gradleProperty("database.password"))
    buildConfigField("gdriveAuth", providers.gradleProperty("gdriveauth"))

}



