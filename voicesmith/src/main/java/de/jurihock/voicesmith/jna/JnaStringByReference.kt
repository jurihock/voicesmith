package de.jurihock.voicesmith.jna

import com.sun.jna.ptr.ByReference

class JnaStringByReference(length: Int = 0) : ByReference(length + 1) {

  var value: String
    get() = pointer.getString(0)
    private set(value) = pointer.setString(0, value)

  init {
    pointer.clear(length.toLong() + 1)
  }

  constructor(string: String) : this(string.length) {
    value = value
  }

  fun value(length: Int) : String {
    return value.substring(0, length)
  }

}
