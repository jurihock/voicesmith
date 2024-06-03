package de.jurihock.voicesmith.plug

import de.jurihock.voicesmith.jna.JnaCallback

interface AudioPlugin : AutoCloseable, JnaCallback {

  val isStarted: Boolean

  fun start()
  fun stop()

}
