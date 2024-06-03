package de.jurihock.voicesmith.jna

import com.sun.jna.ptr.LongByReference

class JnaPointerByReference : LongByReference() {

  val isNotNull: Boolean
    get() = value != 0L

}
