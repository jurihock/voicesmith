package de.jurihock.voicesmith.audio;

import android.content.Context;
import de.jurihock.voicesmith.io.AudioDevice;
import de.jurihock.voicesmith.io.pcm.PcmInDevice;
import de.jurihock.voicesmith.io.pcm.PcmOutDevice;

import java.io.IOException;

/**
 * Provides audio device management routines.
 * Can be mocked for testing purposes.
 */
public class AudioDeviceManager
{
    private final Context context;

    public AudioDeviceManager(Context context)
    {
        this.context = context;
    }

    public AudioDevice getInputDevice(HeadsetMode mode) throws IOException
    {
        return new PcmInDevice(context, mode);

        // TEST: Read input signal from file
        // return new FileInDevice(this, "voicesmith_input.raw");
    }

    public AudioDevice getOutputDevice(HeadsetMode mode) throws IOException
    {
        return new PcmOutDevice(context, mode);

        // TEST: Write output signal to file
        // new FileOutDevice(this, "voicesmith_output.raw");
    }
}
