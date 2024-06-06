package de.jurihock.voicesmith.io

import android.util.Log

inline val Int.toAudioEventCode: AudioEventCode?
  get() = enumValues<AudioEventCode>().firstOrNull {
    it.ordinal == this
  }

inline val AudioEventCode.toLogPriority: Int
  get() = when {
    this.ordinal >= AudioEventCode.Error.ordinal -> Log.ERROR
    this.ordinal >= AudioEventCode.Warning.ordinal -> Log.WARN
    else -> Log.INFO
  }

inline fun AudioEventCode.onError(action: () -> Unit): AudioEventCode {
  if (this.ordinal >= AudioEventCode.Error.ordinal) {
    action()
  }
  return this
}

