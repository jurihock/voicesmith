package de.jurihock.voicesmith.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import de.jurihock.voicesmith.etc.Log
import de.jurihock.voicesmith.plug.AudioPlugin
import de.jurihock.voicesmith.plug.TestAudioPlugin

class AudioService : Service() {

  var plugin: AudioPlugin? = TestAudioPlugin()

  val isStarted: Boolean
    get() = plugin?.isStarted ?: false

  fun start() {
    Log.i("Starting audio plugin")
    plugin?.start()
  }

  fun stop() {
    Log.i("Stopping audio plugin")
    plugin?.stop()
  }

  override fun onCreate() {
    Log.i("Creating audio service")
    plugin?.stop()
  }

  override fun onDestroy() {
    Log.i("Destroying audio service")
    plugin?.close()
  }

  override fun onBind(intent: Intent?): IBinder = bindAudioService()
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = startAudioService()

}
