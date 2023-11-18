rootProject.name = "Circuit Bearker"
include("cave")
include("common")
include("display")

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version ("0.7.0")
}