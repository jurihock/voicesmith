package de.jurihock.voicesmith.io

import android.util.Log

inline val Int.toAudioEventCode: AudioEventCode?
  get() = enumValues<AudioEventCode>().firstOrNull {
    it.ordinal == this
  }

inline val AudioEventCode.toLogPriority: Int
  get() = when {
    this.ordinal >= AudioEventCode.ERROR.ordinal -> Log.ERROR
    this.ordinal >= AudioEventCode.WARNING.ordinal -> Log.WARN
    else -> Log.INFO
  }

inline fun AudioEventCode.onError(action: () -> Unit): AudioEventCode {
  if (this.ordinal >= AudioEventCode.ERROR.ordinal) {
    action()
  }
  return this
}

