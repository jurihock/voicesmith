package de.jurihock.voicesmith.plug

import de.jurihock.voicesmith.etc.Log
import de.jurihock.voicesmith.io.onError
import de.jurihock.voicesmith.io.toAudioEventCode
import de.jurihock.voicesmith.io.toLogPriority
import de.jurihock.voicesmith.jna.JnaCallback
import de.jurihock.voicesmith.jna.JnaPointerByReference
import de.jurihock.voicesmith.jna.JnaResultByReference

open class AudioPlugin(val name: String) : AutoCloseable, JnaCallback {

  private val jna = AudioPluginFactory()
  private val ref = JnaPointerByReference()
  private val res = JnaResultByReference()

  private var callback: ((exception: AudioPluginException) -> Unit)? = null
  private var state: Boolean = false

  val isStarted: Boolean
    get() = state and ref.isNotNull

  init {
    res.result { res ->
      jna.voicesmith_plugin_open(name, this, ref, res)
    }.onFailure { throw it }
  }

  fun setup(input: Int, output: Int, samplerate: Int, blocksize: Int) {
    res.result { res ->
      jna.voicesmith_plugin_setup(
        input, output, samplerate, blocksize,
        ref, res)
    }.onFailure { throw it }
  }

  fun start() {
    res.result { res ->
      jna.voicesmith_plugin_start(ref, res)
    }.onSuccess {
      state = true
    }.onFailure {
      state = false
    }.onFailure { throw it }
  }

  fun stop() {
    res.result { res ->
      jna.voicesmith_plugin_stop(ref, res)
    }.also {
      state = false
    }.onFailure { throw it }
  }

  override fun close() {
    res.result { res ->
      jna.voicesmith_plugin_close(ref, res)
    }.also {
      state = false
    }.onFailure { throw it }
  }

  override fun callback(code: Int, data: String) {
    code.toAudioEventCode?.let { event ->
      Log.log(event.toLogPriority, "${name}: ${event} ${data}")
      event.onError {
        res.result { res ->
          jna.voicesmith_plugin_close(ref, res)
        }.also {
          state = false
        }
        callback?.invoke(AudioPluginException(event, data))
      }
    }
  }

  fun onError(callback: (exception: AudioPluginException) -> Unit) {
    this.callback = callback
  }

}
