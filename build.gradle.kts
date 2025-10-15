plugins {
  alias(libs.plugins.android.gradle.plugin) apply false
  alias(libs.plugins.jetbrains.kotlin.android) apply false
  alias(libs.plugins.jetbrains.kotlin.compose) apply false
}

// fix GitHub CodeQL error:
// Gradle project does not
// define a testClasses goal
tasks.register("testClasses")
