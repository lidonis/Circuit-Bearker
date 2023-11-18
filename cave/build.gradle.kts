plugins {
    application
    alias(libs.plugins.serialization)
    alias(libs.plugins.ktor)
}

application {
    mainClass.set("fr.lidonis.circuitbearker.storage.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get().toInt()))
    }
}

dependencies {
    implementation(project(":common"))
    implementation(libs.bundles.ktor.server)

    testImplementation(libs.bundles.ktor.server.test)
}
