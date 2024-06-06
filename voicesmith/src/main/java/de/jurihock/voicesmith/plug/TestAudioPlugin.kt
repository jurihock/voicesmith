package de.jurihock.voicesmith.plug

import de.jurihock.voicesmith.Defaults
import de.jurihock.voicesmith.etc.Log
import de.jurihock.voicesmith.io.toAudioEventCode
import de.jurihock.voicesmith.io.toLogPriority
import de.jurihock.voicesmith.io.onError
import de.jurihock.voicesmith.jna.JnaPointerByReference
import de.jurihock.voicesmith.jna.JnaResultByReference

class TestAudioPlugin : AudioPlugin {

  private val name = TestAudioPlugin::class.java.simpleName
  private val jna = AudioPluginFactory()
  private val ref = JnaPointerByReference()
  private val res = JnaResultByReference()

  private var isStartedPrivate: Boolean = false
  override val isStarted: Boolean
    get() = isStartedPrivate and ref.isNotNull

  init {
    res.result {
      jna.voicesmith_plugin_open(name, this, ref, it)
    }.onFailure { throw it }
    res.result {
      jna.voicesmith_plugin_setup(
        Defaults.INPUT, Defaults.OUTPUT, Defaults.SAMPLERATE, Defaults.BLOCKSIZE,
        ref, it)
    }.onFailure { throw it }
  }

  override fun start() {
    res.result {
      jna.voicesmith_plugin_start(ref, it)
    }.onSuccess {
      isStartedPrivate = true
    }.onFailure {
      isStartedPrivate = false
    }.onFailure { throw it }
  }

  override fun stop() {
    res.result {
      jna.voicesmith_plugin_stop(ref, it)
    }.also {
      isStartedPrivate = false
    }.onFailure { throw it }
  }

  override fun close() {
    res.result {
      jna.voicesmith_plugin_close(ref, it)
    }.also {
      isStartedPrivate = false
    }.onFailure { throw it }
  }

  override fun callback(code: Int, text: String) {
    code.toAudioEventCode?.let {
      Log.log(it.toLogPriority, "$name: $text")
      it.onError {
        // TODO
      }
    }
  }

}
