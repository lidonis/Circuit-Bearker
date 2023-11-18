@file:Suppress("PropertyName")


group = "fr.lidonis.circuitbearker"
version = "0.0.1"

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.detekt)
}


allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    repositories {
        mavenCentral()
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    dependencies {
        testImplementation(rootProject.libs.junit.jupiter.engine)
        testImplementation(rootProject.libs.bundles.kotest)

        detektPlugins(rootProject.libs.bundles.detekt)
    }

    detekt {
        buildUponDefaultConfig = true
        allRules = false
        autoCorrect = true
        config.setFrom("${project.rootDir}/config/detekt/detekt.yml")
        source.setFrom(
            "src/main/kotlin",
            "src/test/kotlin",
        )
    }
}