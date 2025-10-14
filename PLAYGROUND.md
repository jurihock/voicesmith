# Debugging

## ADB

Use USB wire to install and run the app on your smartphone using Android Studio. Alternatively set up the Wi-Fi connection.

https://developer.android.com/tools/adb

## Logcat

Use the _Logcat_ compatible logger `Log.h`, which supports _fmt_ based string formatting:

```c++
Log::d("2 + 2 = {0}", 4);
```

The logger should be already available in all `.cpp` source files. Otherwise include:

```c++
#include <voicesmith/Source.h>
```

In order to display only log entries produced by the logger, specify following filter string in the _Logcat_ window:

```
voicesmith.java | voicesmith.cpp
```

https://developer.android.com/studio/debug/logcat

# Audio Effect

An audio effect implements the following interface:

```c++
#include <voicesmith/fx/AudioEffect.h>
```

The `reset` callback is triggered once at startup. It provides the audio stream parameters in advance. Not all audio effects need to implement this event.

The `apply` callback is triggered on data block processing. The `input` and `output` buffers are of the same length and address the normalized mono samples.

Use the `ChainEffect` to concatenate multiple audio effects. In case of stereo use `StereoChainEffect`. It will allocate each audio effect instance twice. The `StereoChainEffect` will also handle the signal decoding and channel routing.

# Audio Plugin

An audio plugin implements the following interface:

```c++
#include <voicesmith/fx/AudioPlugin.h>
```

and another one in the _Kotlin_ domain:

```c++
AudioPlugin.kt
```

The purpose of the audio plugin is to manage the `AudioSource`, `AudioSink`, `AudioPipeline`, and `[Stereo]ChainEffect` instances.

It also must provide callbacks `setup` and `set` for parameter synchronization, as well as callbacks `start` and `stop` for state management.

On the _Kotlin_ side the `MainActivity.kt` file provides the UI for the associated audio plugin, which is currently the `TestAudioPlugin` by default.

Currently the `MainActivity.kt` exposes three audio parameters:

- `delay` in milliseconds
- `pitch` in semitones
- `timbre` in semitones

The parameter value range is also specified in the same `.kt` file. In default case, a change to any parameter triggers the `TestAudioPlugin::set` callback in the _C++_ domain. The parameter names and values are passed as a string.

The audio plugin preferences are managed by the `Preferences.kt`. The `AudioFeatures.kt` provides system defaults for `samplesrate` and `blocksize`.
