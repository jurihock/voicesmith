package de.jurihock.voicesmith.etc

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.preference.PreferenceManager
import de.jurihock.voicesmith.io.AudioFeatures

class Preferences(context: Context) {

  private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
  private val features by lazy { AudioFeatures(context) }

  var input: Int
    get() { return preferences.getInt(::input.name, 0) }
    set(value) { preferences.edit().putInt(::input.name, value).commit() }

  var output: Int
    get() { return preferences.getInt(::output.name, 0) }
    set(value) { preferences.edit().putInt(::output.name, value).commit() }

  val samplerate: Int
    get() { return features.samplerate }

  val blocksize: Int
    get() { return features.blocksize }

  var channels: Int
    get() { return preferences.getInt(::channels.name, 2) }
    set(value) { preferences.edit().putInt(::channels.name, value).commit() }

  var delay: Int
    get() { return preferences.getInt(::delay.name, 0) }
    set(value) { preferences.edit().putInt(::delay.name, value).commit() }

  var pitch: Int
    get() { return preferences.getInt(::pitch.name, 0) }
    set(value) { preferences.edit().putInt(::pitch.name, value).commit() }

  var timbre: Int
    get() { return preferences.getInt(::timbre.name, 0) }
    set(value) { preferences.edit().putInt(::timbre.name, value).commit() }

  fun register(listener: OnSharedPreferenceChangeListener) {
    preferences.registerOnSharedPreferenceChangeListener(listener)
  }

  fun unregister(listener: OnSharedPreferenceChangeListener) {
    preferences.unregisterOnSharedPreferenceChangeListener(listener)
  }

}
