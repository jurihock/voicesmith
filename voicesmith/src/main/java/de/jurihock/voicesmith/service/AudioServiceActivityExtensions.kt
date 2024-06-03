package de.jurihock.voicesmith.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

fun AudioServiceActivity.startAudioService() {
  val intent = Intent(this, AudioService::class.java)
  ContextCompat.startForegroundService(this, intent)
}

fun AudioServiceActivity.stopAudioService() {
  val intent = Intent(this, AudioService::class.java)
  stopService(intent)
}

fun AudioServiceActivity.bindAudioService() {
  Intent(this, AudioService::class.java).also { intent ->
    bindService(intent, this, Context.BIND_AUTO_CREATE)
  }
}

fun AudioServiceActivity.unbindAudioService() {
  unbindService(this)
}
