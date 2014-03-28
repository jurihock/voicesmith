/*
 * Voicesmith <http://voicesmith.jurihock.de/>
 *
 * Copyright (c) 2011-2014 Juergen Hock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
