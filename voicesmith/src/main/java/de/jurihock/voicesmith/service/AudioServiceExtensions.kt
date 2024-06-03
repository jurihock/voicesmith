package de.jurihock.voicesmith.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import de.jurihock.voicesmith.MainActivity
import de.jurihock.voicesmith.R

fun AudioService.startAudioService() : Int {
  val id = "${packageName}.notifications"
  val name = getString(R.string.service_notification_channel)
  val title = getString(R.string.service_notification_title)
  val text = getString(R.string.service_notification_text)

  val channel = NotificationChannel(id, name,
    NotificationManager.IMPORTANCE_DEFAULT)

  val notifications = NotificationManagerCompat.from(this)
  notifications.createNotificationChannel(channel)

  val intent = Intent(this, MainActivity::class.java).let {
    PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
  }

  val notification = NotificationCompat.Builder(this, id).apply {
    setContentTitle(title)
    setContentText(text)
    setSmallIcon(R.drawable.voicesmith)
    setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
    setContentIntent(intent)
  }

  ServiceCompat.startForeground(this, 1, notification.build(),
    ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)

  return Service.START_STICKY
}

fun AudioService.bindAudioService() : AudioServiceBinder {
  return AudioServiceBinder(this)
}
