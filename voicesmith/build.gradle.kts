// - Oboe with AAudio API requires at least SDK 27
// - Some particular functions require at least SDK 33
// - AndroidX Compose de facto require at least SDK 34
// - JDK version must match SDK version
//   https://developer.android.com/build/jdks
val min by extra(33)
val sdk by extra(34)
val jdk by extra(17)

// also consider using the appropriate NDK and AGP (libs.versions.toml) version
// https://developer.android.com/build/releases/gradle-plugin#compatibility

plugins {
  alias(libs.plugins.android.gradle.plugin)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.jetbrains.kotlin.compose)
}

android {
  namespace = "de.jurihock.voicesmith"

  defaultConfig {
    applicationId = "de.jurihock.voicesmith"
    versionName = "3.0"
    versionCode = 13
    minSdk = min
    targetSdk = sdk
    ndk {
      // restrict ABIs as supplied by Oboe
      // see https://github.com/google/oboe/blob/main/build_all_android.sh
      abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
    }
    externalNativeBuild {
      cmake {
        // restrict ABIs as supplied by Oboe
        // see https://github.com/google/oboe/blob/main/build_all_android.sh
        abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        // force shared library variant of libc++ as required by Oboe
        arguments += listOf("-DANDROID_STL=c++_shared")
        // enable release build and thus runtime optimizations by default
        arguments += listOf("-DCMAKE_BUILD_TYPE=Release")
        // redirect CPM.cmake cache to avoid re-downloading dependencies for each ABI
        arguments += listOf("-DCPM_SOURCE_CACHE=${project.projectDir}/.cpm")
      }
    }
  }

  compileSdk = sdk
  compileOptions {
    sourceCompatibility = JavaVersion.toVersion(jdk)
    targetCompatibility = JavaVersion.toVersion(jdk)
  }
  // TODO kotlin jvm version
  kotlinOptions {
    @Suppress("DEPRECATION")
    jvmTarget = jdk.toString()
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      isShrinkResources = false
    }
  }

  buildFeatures {
    // enable compose UI feature
    compose = true
    // enable import of prefab dependencies as required by Oboe
    prefab = true
  }

  externalNativeBuild {
    cmake {
      path = file("src/main/cpp/de/jurihock/voicesmith/CMakeLists.txt")
    }
  }
}

// TODO kotlin jvm version
//kotlin {
//  jvmToolchain(jdk)
//}

dependencies {
  implementation(libs.androidx.compose.activity)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.preference)
  implementation(libs.jetbrains.compose.runtime)
  implementation(libs.jna) { artifact { type = "aar" } }
  implementation(libs.oboe)
}
