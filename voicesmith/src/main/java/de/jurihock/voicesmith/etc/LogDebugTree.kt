package de.jurihock.voicesmith.etc

import timber.log.Timber.DebugTree

class LogDebugTree : DebugTree() {
  override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
    super.log(priority, "voicesmith.java", message, throwable)
  }
}
