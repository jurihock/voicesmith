package de.jurihock.voicesmith.io

data class AudioDevice(val id: Int = 0,
                       val name: String = "DEFAULT",
                       val samplerates: List<Int> = emptyList()) {

  override fun toString(): String {
    return "$id: $name (${samplerates.joinToString(",") { it.toString() }})"
  }

}
