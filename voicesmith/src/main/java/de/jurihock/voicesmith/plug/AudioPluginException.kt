package de.jurihock.voicesmith.plug

import de.jurihock.voicesmith.io.AudioEventCode

class AudioPluginException(val event: AudioEventCode, message: String) : Exception(message)
