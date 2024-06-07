package de.jurihock.voicesmith

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import de.jurihock.voicesmith.etc.Game
import de.jurihock.voicesmith.etc.Log
import de.jurihock.voicesmith.etc.Vibrator
import de.jurihock.voicesmith.io.AudioDevices
import de.jurihock.voicesmith.service.AudioServiceActivity
import de.jurihock.voicesmith.ui.IntParameterScreen
import de.jurihock.voicesmith.ui.BigToggleButtonScreen
import de.jurihock.voicesmith.ui.DeviceSelectorScreen
import de.jurihock.voicesmith.ui.MainTheme
import de.jurihock.voicesmith.ui.UI

class MainActivity : AudioServiceActivity() {

  private val devices by lazy { AudioDevices(this) }
  private val game by lazy { Game(this) }
  private val vibrator by lazy { Vibrator(this) }

  private val delay = mutableIntStateOf(0)
  private val pitch = mutableIntStateOf(0)
  private val timbre = mutableIntStateOf(0)
  private val state = mutableStateOf(false)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    try {
      // https://issuetracker.google.com/issues/246845196
      val flags = PackageManager.PackageInfoFlags.of(0)
      val info = packageManager.getPackageInfo(packageName, flags)
      val version = info.versionName
      title = getString(R.string.title_with_version).format(version)
    } catch (exception: NameNotFoundException) {
      Log.e(exception, "Unable to determine the package version!")
    }
    setContent {
      MainTheme {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          topBar = {
            DeviceSelectorScreen(
              modifier = Modifier.padding(Dp(UI.PADDING)),
              textInput = getString(R.string.input),
              textOutput = getString(R.string.output),
              onSelectInputDevice = { onSelectInputDevice() },
              onSelectOutputDevice = { onSelectOutputDevice() })
          },
          bottomBar = {
            BigToggleButtonScreen(
              modifier = Modifier.padding(Dp(UI.PADDING)),
              textOn = getString(R.string.start), textOff = getString(R.string.stop), value = state,
              onToggle = { onStartStopAudioService() })
          }) { padding ->
          Column(modifier = Modifier.padding(padding).padding(Dp(UI.PADDING))) {
            Spacer(modifier = Modifier.weight(1f))
            IntParameterScreen(
              name = getString(R.string.delay), unit = getString(R.string.milliseconds), value = delay,
              min = 0, max = 1000, inc = 50,
              onChange = { delay.intValue = it })
            Spacer(modifier = Modifier.height(Dp(UI.PADDING)))
            IntParameterScreen(
              name = getString(R.string.pitch), unit = getString(R.string.semitones), value = pitch,
              min = -12, max = +12, inc = 1,
              onChange = { pitch.intValue = it })
            Spacer(modifier = Modifier.height(Dp(UI.PADDING)))
            IntParameterScreen(
              name = getString(R.string.timbre), unit = getString(R.string.semitones), value = timbre,
              min = -12, max = +12, inc = 1,
              onChange = { timbre.intValue = it })
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }

  override fun onAudioServiceStarted() {
    state.value = true
    game.on()
    vibrator.on()
  }

  override fun onAudioServiceStopped() {
    state.value = false
    game.off()
    vibrator.off()
  }

  override fun onAudioServiceFailed() {
    state.value = false
    game.off()
    vibrator.error()
  }

  private fun onSelectInputDevice() {
    val devices = devices.inputs.map { it.name }.toTypedArray()
    with(AlertDialog.Builder(this)) {
      setTitle(getString(R.string.select_input_device))
      setSingleChoiceItems(devices, 0) { dialog, index ->
        dialog.dismiss()
      }
      create()
      show()
    }
  }

  private fun onSelectOutputDevice() {
    val devices = devices.outputs.map { it.name }.toTypedArray()
    with(AlertDialog.Builder(this)) {
      setTitle(getString(R.string.select_output_device))
      setSingleChoiceItems(devices, 0) { dialog, index ->
        dialog.dismiss()
      }
      create()
      show()
    }
  }

}

