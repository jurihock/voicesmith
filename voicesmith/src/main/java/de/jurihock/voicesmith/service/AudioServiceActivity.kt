package de.jurihock.voicesmith.service

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import de.jurihock.voicesmith.R
import de.jurihock.voicesmith.etc.Log

abstract class AudioServiceActivity : ComponentActivity(), ServiceConnection {

  private var service: AudioService? = null

  private val permissionToRecordAudio = android.Manifest.permission.RECORD_AUDIO
  private val permissionToPostNotifications = android.Manifest.permission.POST_NOTIFICATIONS

  private val allPermissionsToRequest = arrayOf(
    permissionToRecordAudio,
    permissionToPostNotifications)

  private val permissionRequest = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
      when {
        permissions.getOrDefault(permissionToRecordAudio, false) == true -> {
          Log.i("Record audio permission has been granted")
          Log.i("Starting audio service")
          startAudioService()
          Log.i("Binding audio service")
          bindAudioService()
        }
        permissions.getOrDefault(permissionToRecordAudio, false) == false -> {
          Log.w("Record audio permission has been denied")
          with(AlertDialog.Builder(this)) {
            setTitle(getString(R.string.permissions_rationale_title))
            setMessage(getString(R.string.permissions_rationale_text))
            setPositiveButton(getString(R.string.permissions_rationale_dismiss)) { dialog, _ ->
              dialog.dismiss()
            }
            setNegativeButton(getString(R.string.permissions_rationale_settings)) { dialog, _ ->
              try {
                val action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.parse("package:${packageName}")
                val intent = Intent(action, uri)
                startActivity(intent)
              }
              finally {
                dialog.dismiss()
              }
            }
            create()
            show()
          }
        }
      }
  }

  protected abstract fun onAudioServiceStarted()
  protected abstract fun onAudioServiceStopped()

  fun onStartStopAudioService() {
    if (service == null) {
      permissionRequest.launch(allPermissionsToRequest)
      return
    }

    service?.let {
      if (it.isStarted) {
        it.stop()
        onAudioServiceStopped()
      } else {
        it.start()
        onAudioServiceStarted()
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()

    if (service != null) {
      try {
        Log.i("Unbinding audio service")
        unbindAudioService()
        Log.i("Stopping audio service")
        stopAudioService()
      }
      finally {
        service = null
      }
    }
  }

  final override fun onServiceConnected(serviceName: ComponentName?, serviceBinder: IBinder?) {
    Log.i("Connecting audio service")

    if (serviceBinder == null) {
      Log.e("Invalid binder instance provided by audio service!")
      return
    }

    val binder = serviceBinder as? AudioServiceBinder

    if (binder == null) {
      Log.e("Invalid binder type ${serviceBinder::class.java.simpleName} provided by audio service!")
      return
    }

    service = binder.service

    service?.onServiceError {
      Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
      onAudioServiceStopped()
    }

    onStartStopAudioService()
  }

  final override fun onServiceDisconnected(serviceName: ComponentName?) {
    Log.i("Disconnecting audio service")

    service = null
    onAudioServiceStopped()
  }

}
