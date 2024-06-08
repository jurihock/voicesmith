package de.jurihock.voicesmith.etc

typealias AndroidLog = android.util.Log

class Log {
  companion object {

    const val TAG = "voicesmith.java"
    const val DEBUG = AndroidLog.DEBUG
    const val INFO = AndroidLog.INFO
    const val WARN = AndroidLog.WARN
    const val ERROR = AndroidLog.ERROR

    fun d(msg: String) {
      AndroidLog.d(TAG, msg)
    }

    fun i(msg: String) {
      AndroidLog.i(TAG, msg)
    }

    fun w(msg: String) {
      AndroidLog.w(TAG, msg)
    }

    fun e(msg: String) {
      AndroidLog.e(TAG, msg)
    }

    fun e(msg: String, thr: Throwable) {
      AndroidLog.e(TAG, msg, thr)
    }

    fun e(thr: Throwable) {
      AndroidLog.e(TAG, thr.message, thr)
    }

    fun p(prio: Int, msg: String) {
      when(prio) {
        DEBUG -> AndroidLog.d(TAG, msg)
        INFO -> AndroidLog.i(TAG, msg)
        WARN -> AndroidLog.w(TAG, msg)
        ERROR -> AndroidLog.e(TAG, msg)
        else -> throw IllegalArgumentException(
          "Invalid log priority ${prio}!")
      }
    }

  }
}
