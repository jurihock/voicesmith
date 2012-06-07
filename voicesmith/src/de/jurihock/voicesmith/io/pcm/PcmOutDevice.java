/*******************************************************************************
 * src/de/jurihock/voicesmith/io/pcm/PcmOutDevice.java
 * is part of the Voicesmith project
 * <http://voicesmith.jurihock.de>
 * 
 * Copyright (C) 2011-2012 Juergen Hock
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
 ******************************************************************************/

package de.jurihock.voicesmith.io.pcm;

import java.io.IOException;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioTrack;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.Utils;

public final class PcmOutDevice extends PcmDevice
{
	private AudioTrack	output	= null;

	public PcmOutDevice(Context context) throws IOException
	{
		super(context);

		this.setAudioSource(Preferences.PCM_OUT_SOURCE);
		this.setChannels(AudioFormat.CHANNEL_OUT_MONO); // DON'T CHANGE!
		this.setEncoding(AudioFormat.ENCODING_PCM_16BIT); // DON'T CHANGE!
		this.setMinBufferSize(AudioTrack.getMinBufferSize(
			getSampleRate(), getChannels(), getEncoding()));

		this.setBufferSize(new Preferences(context).getPcmBufferSize());
		Utils.log("PCM OUT buffer size is %s.", this.getBufferSize());

		output = new AudioTrack(getAudioSource(), getSampleRate(),
			getChannels(), getEncoding(), getBufferSize(),
			AudioTrack.MODE_STREAM);

		if (output.getState() != AudioTrack.STATE_INITIALIZED)
		{
			dispose();
			throw new IOException(
				"Unable to initialize an AudioTrack instance!");
		}
	}

	@Override
	public int write(short[] buffer, int offset, int count)
	{
		return output.write(buffer, offset, count);
	}

	@Override
	public void flush()
	{
		output.flush();
	}

	@Override
	public void start()
	{
		output.play();
	}

	@Override
	public void stop()
	{
		output.stop();
	}

	@Override
	public void dispose()
	{
		if (output != null)
		{
			output.release();
			output = null;
		}
	}
}
