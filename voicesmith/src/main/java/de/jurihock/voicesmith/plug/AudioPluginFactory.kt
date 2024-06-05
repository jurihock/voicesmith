package de.jurihock.voicesmith.plug

import com.sun.jna.Native
import de.jurihock.voicesmith.jna.JnaCallback
import de.jurihock.voicesmith.jna.JnaPointerByReference
import de.jurihock.voicesmith.jna.JnaResultByReference

@Suppress("KotlinJniMissingFunction", "FunctionName")
open class AudioPluginFactory {

  init {
    Native.register(AudioPluginFactory::class.java, "voicesmith")
  }

  external fun voicesmith_plugin_open(name: String, callback: JnaCallback, pointer: JnaPointerByReference, result: JnaResultByReference) : Boolean
  external fun voicesmith_plugin_setup(input: Int, output: Int, samplerate: Int, blocksize: Int, pointer: JnaPointerByReference, result: JnaResultByReference) : Boolean
  external fun voicesmith_plugin_start(pointer: JnaPointerByReference, result: JnaResultByReference) : Boolean
  external fun voicesmith_plugin_stop(pointer: JnaPointerByReference, result: JnaResultByReference) : Boolean
  external fun voicesmith_plugin_close(pointer: JnaPointerByReference, result: JnaResultByReference) : Boolean

}
