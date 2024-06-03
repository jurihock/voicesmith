package de.jurihock.voicesmith.jna

import com.sun.jna.Structure

@Structure.FieldOrder("okay", "error", "maxErrorLength", "minErrorLength")
class JnaResultByReference(maxErrorLength: Int = 1000) : Structure(), Structure.ByReference {

  @JvmField var okay = true
  @JvmField var error = JnaStringByReference(maxErrorLength)
  @JvmField var maxErrorLength = maxErrorLength
  @JvmField var minErrorLength = 0

  fun reset() {
    okay = true
    minErrorLength = 0
  }

  inline fun <T> result(action: (JnaResultByReference) -> T): Result<T> {
    return try {
      reset()
      action(this).let {
        if (okay) Result.success(it)
        else Result.failure(JnaException(toString()))
      }
    } catch (exception: Throwable) {
      Result.failure(exception)
    }
  }

  override fun toString() : String {
    return error.value(minErrorLength)
  }

}
