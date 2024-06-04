package de.jurihock.voicesmith.io

data class AudioDevice(val id: Int = 0,
                       val name: String = "DEFAULT",
                       val samplerates: List<Int> = emptyList()) {

  override fun toString(): String {
    val sr = samplerates.joinToString(",") { it.toString() }
    return "$id: $name (${sr.ifEmpty { "n.a." }})"
  }

}
