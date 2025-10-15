package de.jurihock.voicesmith.io

import android.app.AlertDialog
import android.content.Context
import android.media.AudioManager
import de.jurihock.voicesmith.R

class AudioDevices(val context: Context) {

  private val audio: AudioManager? = context.getSystemService(AudioManager::class.java)

  private val types: Map<Int, String> =
    with(android.media.AudioDeviceInfo::class.java) {
      declaredFields
        .filter { it.name.startsWith("TYPE_") }
        .associate { Pair(it.getInt(this), it.name.split("_").drop(1).joinToString(" ")) }
    }

  val inputs: List<AudioDevice>
    get() {
      val flags = AudioManager.GET_DEVICES_INPUTS
      return listOf(AudioDevice()) + (audio?.getAudioDevices(flags, types) ?: emptyList())
    }

  val outputs: List<AudioDevice>
    get() {
      val flags = AudioManager.GET_DEVICES_OUTPUTS
      return listOf(AudioDevice()) + (audio?.getAudioDevices(flags, types) ?: emptyList())
    }

}

fun AudioManager.getAudioDevices(flags: Int, types: Map<Int, String>) : List<AudioDevice> {
  return getDevices(flags).let { devices ->
    devices
      .filter { it.id != 0 }
      .map { device ->
        AudioDevice(
          device.id,
          listOf(types.getValue(device.type), device.address)
            .filter { it.isNotEmpty() }
            .joinToString(" ")
            .uppercase(),
          device.sampleRates.sorted(),
          device.channelCounts.sorted())
      }
  }
}

fun AudioDevices.selectInputDevice(id: Int, callback: (id: Int) -> Unit) {
  val devices = inputs
  val names = devices.map { it.name }.toTypedArray()
  val oldindex = devices.indexOfFirst { it.id == id }.coerceAtLeast(0)

  with(AlertDialog.Builder(context)) {
    setTitle(context.getString(R.string.select_input_device))
    setSingleChoiceItems(names, oldindex) { dialog, newindex ->
      dialog.dismiss()
      if (newindex != oldindex) {
        callback(devices[newindex].id)
      }
    }
    create()
    show()
  }
}

fun AudioDevices.selectOutputDevice(id: Int, callback: (id: Int) -> Unit) {
  val devices = outputs
  val names = devices.map { it.name }.toTypedArray()
  val oldindex = devices.indexOfFirst { it.id == id }.coerceAtLeast(0)

  with(AlertDialog.Builder(context)) {
    setTitle(context.getString(R.string.select_output_device))
    setSingleChoiceItems(names, oldindex) { dialog, newindex ->
      dialog.dismiss()
      if (newindex != oldindex) {
        callback(devices[newindex].id)
      }
    }
    create()
    show()
  }
}

fun AudioDevices.selectChannels(id: Int, callback: (id: Int) -> Unit) {
  val names = arrayOf("MONO", "STEREO")
  val oldindex = id - 1

  with(AlertDialog.Builder(context)) {
    setTitle(context.getString(R.string.select_channels))
    setSingleChoiceItems(names, oldindex) { dialog, newindex ->
      dialog.dismiss()
      if (newindex != oldindex) {
        callback(newindex + 1)
      }
    }
    create()
    show()
  }
}
