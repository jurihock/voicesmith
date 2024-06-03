package de.jurihock.voicesmith

import android.app.Application
import android.content.pm.ApplicationInfo
import de.jurihock.voicesmith.etc.Log
import de.jurihock.voicesmith.etc.LogDebugTree
import de.jurihock.voicesmith.io.AudioDevices
import de.jurihock.voicesmith.io.AudioFeatures

class Main : Application() {

  private fun log() {
    val debug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    if (debug) Log.plant(LogDebugTree())
    else Log.plant(LogDebugTree())
  }

  private fun features() {
    val features = AudioFeatures(this)
    Log.i("~ Features ~")
    Log.i("Low Latency Feature ${ if (features.hasLowLatencyFeature) ":)" else ":(" } ")
    Log.i("Pro Feature ${ if (features.hasProFeature) ":)" else ":(" }")
  }

  private fun devices() {
    val devices = AudioDevices(this)
    Log.i("~ Inputs ~")
    for (device in devices.inputs) {
      Log.i(device.toString())
    }
    Log.i("~ Outputs ~")
    for (device in devices.outputs) {
      Log.i(device.toString())
    }
  }

  override fun onCreate() {
    super.onCreate()
    log()
    features()
    devices()
  }

}
