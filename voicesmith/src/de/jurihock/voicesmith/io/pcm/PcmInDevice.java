/*******************************************************************************
 * src/de/jurihock/voicesmith/io/pcm/PcmInDevice.java
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
import android.media.AudioRecord;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.Utils;

public final class PcmInDevice extends PcmDevice
{
	private AudioRecord	input	= null;

	public PcmInDevice(Context context) throws IOException
	{
		super(context);

		this.setAudioSource(Preferences.PCM_IN_SOURCE);
		this.setChannels(AudioFormat.CHANNEL_IN_MONO); // DON'T CHANGE!
		this.setEncoding(AudioFormat.ENCODING_PCM_16BIT); // DON'T CHANGE!
		this.setMinBufferSize(AudioRecord.getMinBufferSize(
			getSampleRate(), getChannels(), getEncoding()));

		this.setBufferSize(new Preferences(context).getPcmBufferSize());
		Utils.log("PCM IN buffer size is %s.", this.getBufferSize());

		input = new AudioRecord(getAudioSource(), getSampleRate(),
			getChannels(), getEncoding(), getBufferSize());

		if (input.getState() != AudioRecord.STATE_INITIALIZED)
		{
			dispose();
			throw new IOException(
				"Unable to initialize an AudioRecord instance!");
		}
	}

	@Override
	public int read(short[] buffer, int offset, int count)
	{
		return input.read(buffer, offset, count);
	}

	@Override
	public void start()
	{
		input.startRecording();
	}

	@Override
	public void stop()
	{
		input.stop();
	}

	@Override
	public void dispose()
	{
		if (input != null)
		{
			input.release();
			input = null;
		}
	}
}
