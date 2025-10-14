// use appropriate version of Gradle, JDK, NDK, and AGP
// for a particular version of Android Studio
// e.g. Narwhal 4 Feature Drop | 2025.1.4
// - Gradle >= 8.13
// - JDK >= 17
// - NDK >= N/A (27.0.12077973)
// - AGP >= 8.13.0
//   https://developer.android.com/build/releases/gradle-plugin#compatibility

// Android SDK and JVM compatibility considerations
// - Oboe with AAudio API requires at least SDK 27 (consider as minSdk)
// - Samsung Galaxy A13 5G is our main device running Android 14 (consider as targetSdk)
// - set compileSdk as high as required by project dependencies
// - set Java compatibility version according to
//   https://developer.android.com/build/jdks
//   https://developer.android.com/studio/write/java8-support
val sdk by extra(intArrayOf(31, 34, 36)) // minSdk <= targetSdk <= compileSdk
val jvm by extra(1.8) // sourceCompatibility == targetCompatibility == jvmTarget

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
    minSdk = sdk[0]
    targetSdk = sdk[1]
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

  compileSdk = sdk[2]
  compileOptions {
    sourceCompatibility = JavaVersion.toVersion(jvm)
    targetCompatibility = JavaVersion.toVersion(jvm)
  }
  kotlinOptions {
    // consider jvmTarget as non-officially deprecated
    // https://stackoverflow.com/q/77363060
    // https://youtrack.jetbrains.com/issue/KT-27301#focus=Comments-27-6565858.0-0
    @Suppress("DEPRECATION")
    jvmTarget = jvm.toString()
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

dependencies {
  implementation(libs.androidx.activity)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.preference)
  implementation(libs.jetbrains.compose.runtime)
  implementation(libs.jna)
  implementation(libs.oboe)
}
