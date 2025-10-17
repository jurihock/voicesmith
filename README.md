# Voicesmith

![languages](https://img.shields.io/badge/languages-Kotlin%20C%2B%2B-blue)
![license](https://img.shields.io/github/license/jurihock/voicesmith?color=blue)
![build](https://img.shields.io/github/actions/workflow/status/jurihock/voicesmith/build.yml?branch=main&label=build)
![apk](https://img.shields.io/github/actions/workflow/status/jurihock/voicesmith/apk.yml?branch=main&label=apk)
![release](https://img.shields.io/github/v/release/jurihock/voicesmith?color=gold)

Voicesmith is a voice changer app for Android OS. Internally it utilizes the [stftPitchShift](https://github.com/jurihock/stftPitchShift) engine to perform pitch and timbre shifting in real time.

> [!NOTE]
> The current working copy, which is not yet complete, is on the [main](https://github.com/jurihock/voicesmith/tree/main) branch of this repository.
> The previous version of this app is still available on the [master](https://github.com/jurihock/voicesmith/tree/master) branch.

## Compatibility

This app should work as expected on the
[Samsung Galaxy A13 5G](https://en.wikipedia.org/wiki/Samsung_Galaxy_A13)
smartphone running [Android 14](https://en.wikipedia.org/wiki/Android_14).
There is no official support for other devices or operating system versions.

## Installation

Unfortunately, there are currently no plans to publish it on app stores such as _F-Droid_ or _Google Play_.
Please use the _APK_ file provided in the [releases](https://github.com/jurihock/voicesmith/releases) or attached to the [latest commits](https://github.com/jurihock/voicesmith/actions/workflows/apk.yml).

[How to Download & Install an APK on Android?](https://www.wikihow.com/Install-APK-Files-on-Android)

## Building from sources

- Using [Android Studio](https://developer.android.com/studio/run)
- Using [Gradle](https://developer.android.com/build/building-cmdline) from the command line

Install _NDK_ and _CMake_ in Android Studio using the bundled _SDK Manager_. I suppose you have to install the _Java JDK_ as well.

## License

*Voicesmith* is licensed under the terms of the GPL license.
For details please refer to the accompanying [LICENSE](LICENSE)
file distributed with *Voicesmith*.
