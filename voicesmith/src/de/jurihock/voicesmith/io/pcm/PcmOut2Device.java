/*******************************************************************************
 * src/de/jurihock/voicesmith/io/pcm/PcmOut2Device.java
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
import android.media.AudioManager;
import android.media.AudioTrack;
import de.jurihock.voicesmith.Utils;

@Deprecated
public class PcmOut2Device extends PcmDevice
{
	private AudioTrack	output			= null;
	private short[]		bufferStereo	= null;

	public PcmOut2Device(Context context) throws IOException
	{
		super(context);
		
		this.setAudioSource(AudioManager.STREAM_MUSIC);
		this.setChannels(AudioFormat.CHANNEL_OUT_STEREO);
		this.setEncoding(AudioFormat.ENCODING_PCM_16BIT);
		this.setMinBufferSize(AudioTrack.getMinBufferSize(
			getSampleRate(), getChannels(), getEncoding()));

//		this.setBufferSize(Math.max(
//			Defaults.PCM_OUT_BUFFER_SIZE,
//			getMinBufferSize()));

		Utils.log("PCM Out buffer size %s", this.getBufferSize());

		output = new AudioTrack(getAudioSource(), getSampleRate(),
			getChannels(), getEncoding(), getBufferSize(),
			AudioTrack.MODE_STREAM);

		if (output.getState() != AudioTrack.STATE_INITIALIZED)
		{
			dispose();
			throw new IOException("Unable to initialize AudioTrack instance!");
		}
	}

	@Override
	public int write(short[] bufferMono, int offset, int count)
	{
		if (bufferStereo == null || bufferStereo.length != bufferMono.length)
		{
			bufferStereo = new short[bufferMono.length * 2];
		}

		for (int i = 0; i < bufferMono.length; i++)
		{
			bufferStereo[2 * i] = bufferMono[i];
			bufferStereo[2 * i + 1] = bufferMono[i];
		}

		return output.write(bufferStereo, offset * 2, count * 2) / 2;
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
