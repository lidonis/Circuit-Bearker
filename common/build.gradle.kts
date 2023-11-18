plugins {
  alias(libs.plugins.serialization)
}

dependencies {
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.ktor.resources)
}
