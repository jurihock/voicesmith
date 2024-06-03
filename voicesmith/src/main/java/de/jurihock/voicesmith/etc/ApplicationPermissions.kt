package de.jurihock.voicesmith.etc

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ApplicationPermissions(val activity: Activity) {

  fun granted(grantResults: IntArray) : Boolean {
    return grantResults.isNotEmpty() and grantResults.all {
      grantResult -> grantResult == PackageManager.PERMISSION_GRANTED
    }
  }

  fun granted(permission: String) : Boolean {
    return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
  }

  fun rationale(permission: String) : Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
  }

  fun request(permission: String, requestCode: Int) {
    ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
  }

}
