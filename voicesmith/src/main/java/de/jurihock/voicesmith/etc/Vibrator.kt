package de.jurihock.voicesmith.etc

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

class Vibrator(context: Context, var enabled: Boolean = true) {

  private val vibrator: Vibrator? = context.getSystemService(Vibrator::class.java)

  private val effectOn = VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
  private val effectOff = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
  private val effectError = VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)

  fun on() {
    if (enabled) {
      vibrator?.vibrate(effectOn)
    }
  }

  fun off() {
    if (enabled) {
      vibrator?.vibrate(effectOff)
    }
  }

  fun error() {
    if (enabled) {
      vibrator?.vibrate(effectError)
    }
  }

}
