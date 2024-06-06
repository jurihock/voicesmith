package de.jurihock.voicesmith.io

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager

class AudioFeatures(context: Context) {

  private val audio: AudioManager? = context.getSystemService(AudioManager::class.java)

  val samplerate : Int
    get() = audio?.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)?.toInt() ?: 0

  val blocksize : Int
    get() = audio?.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)?.toInt() ?: 0

  val hasLowLatencyFeature : Boolean = context.packageManager.hasSystemFeature(
    PackageManager.FEATURE_AUDIO_LOW_LATENCY)

  val hasProFeature : Boolean = context.packageManager.hasSystemFeature(
    PackageManager.FEATURE_AUDIO_PRO)

}
