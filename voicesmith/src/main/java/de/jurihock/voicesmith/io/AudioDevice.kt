package de.jurihock.voicesmith.io

data class AudioDevice(val id: Int = 0,
                       val name: String = "DEFAULT",
                       val samplerates: List<Int> = emptyList(),
                       val channels: List<Int> = emptyList()) {

  override fun toString(): String {
    val sr = samplerates.joinToString(",") { it.toString() }
    val ch = channels.joinToString(",") { it.toString() }
    return "$id: $name (sr: ${sr.ifEmpty { "n.a." }} | ch: ${ch.ifEmpty { "n.a." }})"
  }

}
