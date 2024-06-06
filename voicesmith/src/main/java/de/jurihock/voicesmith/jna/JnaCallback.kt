package de.jurihock.voicesmith.jna

import com.sun.jna.Callback

interface JnaCallback : Callback {

  fun callback(code: Int, data: String)

}
