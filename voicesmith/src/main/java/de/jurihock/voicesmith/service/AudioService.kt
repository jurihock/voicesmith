package de.jurihock.voicesmith.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import de.jurihock.voicesmith.etc.Log
import de.jurihock.voicesmith.io.AudioFeatures
import de.jurihock.voicesmith.plug.AudioPlugin
import de.jurihock.voicesmith.plug.TestAudioPlugin

class AudioService : Service() {

  private var callback: ((exception: Throwable) -> Unit)? = null
  private var plugin: AudioPlugin? = null

  private val features by lazy { AudioFeatures(this) }

  val isStarted: Boolean
    get() = plugin?.isStarted ?: false

  fun start() {
    Log.i("Starting audio plugin")
    plugin?.onError { onPluginError(it) }
    try {
      plugin?.start()
    } catch (exception: Throwable) {
      onPluginError(exception)
    }
  }

  fun stop() {
    Log.i("Stopping audio plugin")
    try {
      plugin?.stop()
    } catch (exception: Throwable) {
      Log.e(exception)
    }
  }

  override fun onCreate() {
    Log.i("Creating audio service")
    try {
      plugin = TestAudioPlugin()
      plugin?.setup(0, 0, features.samplerate, features.blocksize)
    } catch (exception: Throwable) {
      Log.e(exception)
    }
  }

  override fun onDestroy() {
    Log.i("Destroying audio service")
    try {
      plugin?.close()
      plugin = null
    } catch (exception: Throwable) {
      Log.e(exception)
    }
  }

  override fun onBind(intent: Intent?): IBinder = bindAudioService()
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = startAudioService()

  fun onServiceError(callback: (exception: Throwable) -> Unit) {
    this.callback = callback
  }

  private fun onPluginError(exception: Throwable) {
    try {
      plugin?.stop()
    } finally {
      callback?.invoke(exception)
    }
  }

}
