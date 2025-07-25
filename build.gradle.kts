import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin

ext {
    set("android.buildToolsVersion", "36.0.0")
    set("android.compileSdk", 36)
    set("android.targetSdk", 36)
    set("android.minSdk", 26)
    set("android.javaVersion", JavaVersion.VERSION_11)
    set("kotlin.jvmTarget", JvmTarget.JVM_11)
}

plugins {
    alias(libs.plugins.google.ksp) apply false

    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.androidx.room) apply false

    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false

    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    alias(libs.plugins.rikka.refine) apply false

    alias(libs.plugins.benmanes.version)
    alias(libs.plugins.littlerobots.version)
}

// https://kotlinlang.org/docs/js-project-setup.html#use-pre-installed-node-js
rootProject.plugins.withType<NodeJsPlugin> {
    project.extensions.getByType<NodeJsEnvSpec>().download = false
}

val normalVersionRegex by lazy { "^[0-9\\.]+".toRegex() }
fun isSameTypeVersion(currentVersion: String, newVersion: String): Boolean {
    if (normalVersionRegex.matches(currentVersion)) {
        return normalVersionRegex.matches(newVersion)
    }
    arrayOf("alpha", "beta", "dev", "rc").forEach { v ->
        if (currentVersion.contains(v, true)) {
            return newVersion.contains(v, true)
        }
    }
    throw IllegalArgumentException("Unknown version type: $currentVersion -> $newVersion")
}
tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    rejectVersionIf {
        !isSameTypeVersion(currentVersion, candidate.version)
    }
}
// ./gradlew versionCatalogUpdate --interactive
projectDir.resolve("./gradle/libs.versions.updates.toml").apply {
    if (exists()) {
        delete()
    }
}
