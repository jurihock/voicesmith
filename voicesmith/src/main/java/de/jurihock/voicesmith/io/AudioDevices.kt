package de.jurihock.voicesmith.io

import android.content.Context
import android.media.AudioManager

class AudioDevices(context: Context) {

  private val audio: AudioManager? = context.getSystemService(AudioManager::class.java)

  private val types: Map<Int, String> =
    with(android.media.AudioDeviceInfo::class.java) {
      declaredFields
        .filter { it.name.startsWith("TYPE_") }
        .associate { Pair(it.getInt(this), it.name.split("_").drop(1).joinToString(" ")) }
    }

  val inputs: List<AudioDevice>
    get() {
      return listOf(AudioDevice()) +
        (audio?.getAudioDevices(AudioManager.GET_DEVICES_INPUTS, types) ?: emptyList())
    }

  val outputs: List<AudioDevice>
    get() {
      return listOf(AudioDevice()) +
        (audio?.getAudioDevices(AudioManager.GET_DEVICES_OUTPUTS, types) ?: emptyList())
    }

}

fun AudioManager.getAudioDevices(flags: Int, types: Map<Int, String>) : List<AudioDevice> {
  return this.getDevices(flags).let { devices ->
    devices
      .filter { it.id != 0 }
      .map { device ->
        AudioDevice(
          device.id,
          listOf(types.getValue(device.type), device.address)
            .filter { it.isNotEmpty() }
            .joinToString(" ")
            .uppercase(),
          device.sampleRates.sorted())
      }
  }
}
