package de.jurihock.voicesmith.plug

import android.util.Log

enum class AudioPluginCallcode {
  Info,
  Warning,
  Error
}

inline val Int.callcode: AudioPluginCallcode?
  get() = enumValues<AudioPluginCallcode>().firstOrNull {
    it.ordinal == this
  }

inline val AudioPluginCallcode.logprio: Int
  get() = when {
    this.ordinal >= AudioPluginCallcode.Error.ordinal -> Log.ERROR
    this.ordinal >= AudioPluginCallcode.Warning.ordinal -> Log.WARN
    else -> Log.INFO
  }

inline fun AudioPluginCallcode.onError(action: () -> Unit): AudioPluginCallcode {
  if (this.ordinal >= AudioPluginCallcode.Error.ordinal) {
    action()
  }
  return this
}
