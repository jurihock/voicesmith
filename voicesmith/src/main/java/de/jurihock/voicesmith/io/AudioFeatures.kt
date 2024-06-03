package de.jurihock.voicesmith.io

import android.content.Context
import android.content.pm.PackageManager

class AudioFeatures(val context: Context) {

  val hasLowLatencyFeature = context.packageManager.hasSystemFeature(
    PackageManager.FEATURE_AUDIO_LOW_LATENCY)

  val hasProFeature = context.packageManager.hasSystemFeature(
    PackageManager.FEATURE_AUDIO_PRO)

}
