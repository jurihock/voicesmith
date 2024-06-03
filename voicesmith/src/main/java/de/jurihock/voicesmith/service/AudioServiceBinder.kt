package de.jurihock.voicesmith.service

import android.os.Binder

class AudioServiceBinder(val service: AudioService) : Binder()
