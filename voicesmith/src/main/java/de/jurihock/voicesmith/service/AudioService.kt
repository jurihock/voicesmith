package de.jurihock.voicesmith.service

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import de.jurihock.voicesmith.etc.Log
import de.jurihock.voicesmith.etc.Preferences
import de.jurihock.voicesmith.plug.AudioPlugin
import de.jurihock.voicesmith.plug.TestAudioPlugin

class AudioService : Service(), SharedPreferences.OnSharedPreferenceChangeListener {

  private val preferences by lazy { Preferences(this) }

  private var error: ((exception: Throwable) -> Unit)? = null
  private var plugin: AudioPlugin? = null

  val isStarted: Boolean
    get() = plugin?.isStarted ?: false

  fun start() {
    Log.i("Starting audio plugin")
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
      plugin?.onError { onPluginError(it) }
      sync()
    } catch (exception: Throwable) {
      Log.e(exception)
    }

    Log.i("Subscribing application preferences")
    preferences.register(this)
  }

  override fun onDestroy() {
    Log.i("Unsubscribing application preferences")
    preferences.unregister(this)

    Log.i("Destroying audio service")
    try {
      plugin?.close()
      plugin = null
    } catch (exception: Throwable) {
      Log.e(exception)
    }
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, name: String?) {
    when(name) {
      "input" -> reset()
      "output" -> reset()
      "samplerate" -> reset()
      "blocksize" -> reset()
      "delay" -> plugin?.set("delay", preferences.delay.toString())
      "pitch" -> plugin?.set("pitch", preferences.pitch.toString())
      "timbre" -> plugin?.set("timbre", preferences.timbre.toString())
    }
  }

  override fun onBind(intent: Intent?): IBinder = bindAudioService()
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = startAudioService()

  fun onServiceError(callback: (exception: Throwable) -> Unit) {
    error = callback
  }

  private fun onPluginError(exception: Throwable) {
    try {
      plugin?.stop()
    } finally {
      error?.invoke(exception)
    }
  }

  private fun sync() {
    Log.i("Syncing audio plugin parameters")
    try {
      plugin?.setup(
        preferences.input,
        preferences.output,
        preferences.samplerate,
        preferences.blocksize)
      plugin?.set("delay", preferences.delay.toString())
      plugin?.set("pitch", preferences.pitch.toString())
      plugin?.set("timbre", preferences.timbre.toString())
    } catch (exception: Throwable) {
      Log.e(exception)
    }
  }

  private fun reset() {
    Log.i("Resetting audio plugin")
    val restart = isStarted
    stop()
    sync()
    if (restart) {
      start()
    }
  }

}
