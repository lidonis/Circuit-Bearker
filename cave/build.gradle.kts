plugins {
  application
  alias(libs.plugins.ktor)
}

application {
  mainClass.set("fr.lidonis.circuitbearker.storage.ApplicationKt")

  val isDevelopment: Boolean = project.ext.has("development")
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
  implementation(project(":common"))
  implementation(libs.bundles.ktor.server)

  testImplementation(libs.bundles.ktor.server.test)
}
