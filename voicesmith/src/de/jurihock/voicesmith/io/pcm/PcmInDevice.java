/*******************************************************************************
 * Voicesmith <http://voicesmith.jurihock.de/>
 * Copyright (c) 2011-2012 Juergen Hock
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
import android.media.MediaRecorder;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.Utils;
import de.jurihock.voicesmith.audio.HeadsetMode;

public class PcmInDevice extends PcmDevice
{
    private static final int	WIRED_HEADPHONES_SOURCE		= MediaRecorder.AudioSource.CAMCORDER;
	private static final int	WIRED_HEADSET_SOURCE		= MediaRecorder.AudioSource.DEFAULT; // or MIC
	private static final int	BLUETOOTH_HEADSET_SOURCE	= MediaRecorder.AudioSource.DEFAULT; // or MIC

	private AudioRecord			input						= null;

	public PcmInDevice(Context context, HeadsetMode headsetMode)
		throws IOException
	{
		super(context);

		switch (headsetMode)
		{
        case WIRED_HEADPHONES:
            setAudioSource(WIRED_HEADPHONES_SOURCE);
            break;
		case WIRED_HEADSET:
			setAudioSource(WIRED_HEADSET_SOURCE);
			break;
		case BLUETOOTH_HEADSET:
			setSampleRate(8000);
			new Utils(context).log("Sample rate changed to 8000 Hz.");
			setAudioSource(BLUETOOTH_HEADSET_SOURCE);
			break;
		default:
			throw new IOException("Unknown HeadsetMode!");
		}

		init(context);
	}

	public PcmInDevice(Context context, int sampleRate) throws IOException
	{
		super(context, sampleRate);
		setAudioSource(WIRED_HEADSET_SOURCE);
		init(context);
	}

	public PcmInDevice(Context context) throws IOException
	{
		super(context);
		setAudioSource(WIRED_HEADSET_SOURCE);
		init(context);
	}

	private void init(Context context) throws IOException
	{
		setChannels(AudioFormat.CHANNEL_IN_MONO); // DON'T CHANGE!
		setEncoding(AudioFormat.ENCODING_PCM_16BIT); // DON'T CHANGE!
		setMinBufferSize(AudioRecord.getMinBufferSize(
			getSampleRate(), getChannels(), getEncoding()));

		if (getMinBufferSize() == AudioRecord.ERROR_BAD_VALUE ||
			getMinBufferSize() == AudioRecord.ERROR)
		{
			throw new IOException(
				"Unable to determine the MinBufferSize for AudioRecord!");
		}

		setBufferSize(new Preferences(context)
			.getPcmBufferSize(getSampleRate()));
		new Utils(context).log("PCM IN buffer size is %s.", getBufferSize());

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
