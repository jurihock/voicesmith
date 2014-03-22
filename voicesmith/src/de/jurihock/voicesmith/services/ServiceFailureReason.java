package de.jurihock.voicesmith.services;

public enum ServiceFailureReason
{
    UnknownReason,
    AudioDeviceInitialization,
    InitialHeadsetAvailability,
    WiredHeadsetAvailability,
    BluetoothHeadsetAvailability
}
