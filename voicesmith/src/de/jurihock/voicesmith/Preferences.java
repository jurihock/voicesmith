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

package de.jurihock.voicesmith;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.preference.PreferenceManager;

public final class Preferences
{
	// TODO: Try different audio sources
	// public static final int PCM_IN_SOURCE = MediaRecorder.AudioSource.MIC;
	// public static final int PCM_IN_SOURCE =
	// MediaRecorder.AudioSource.VOICE_CALL;
	// public static final int PCM_IN_SOURCE =
	// MediaRecorder.AudioSource.VOICE_DOWNLINK;
	// public static final int PCM_IN_SOURCE =
	// MediaRecorder.AudioSource.VOICE_UPLINK;
	// public static final int PCM_OUT_SOURCE = AudioManager.STREAM_MUSIC;
	// public static final int PCM_OUT_SOURCE = AudioManager.STREAM_VOICE_CALL;

	public static final String		DATASTORE_DIR	= "Android/data/de.jurihock.voicesmith";
	public static final String		VOICEBANK_DIR	= DATASTORE_DIR
														+ "/voicebank";
	public static final String		RECORDS_FILE	= VOICEBANK_DIR
														+ "/records.xml";

	private final SharedPreferences	preferences;

	public Preferences(Context context)
	{
		preferences = PreferenceManager
			.getDefaultSharedPreferences(context);

		PreferenceManager.setDefaultValues(
			context, R.xml.preferences, false);
	}

	public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener)
	{
		preferences.registerOnSharedPreferenceChangeListener(listener);
	}

	public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener)
	{
		preferences.unregisterOnSharedPreferenceChangeListener(listener);
	}

	public void reset()
	{
		preferences.edit().clear().commit();
	}

	public int getVolumeLevel()
	{
		return Integer.parseInt(
			preferences.getString("VolumeLevel", "50"));
	}

	public boolean setVolumeLevel(int value)
	{
		if ((0 <= value) && (value <= 100))
		{
			return preferences.edit()
				.putString("VolumeLevel",
					Integer.toString(value))
				.commit();
		}

		return false;
	}

	public int getSoundAmplification()
	{
		return Integer.parseInt(
			preferences.getString("SoundAmplification", "6"));
	}

	public boolean isReduceNoise()
	{
		return preferences.getBoolean("ReduceNoise", true);
	}

	public int getSampleRate()
	{
		return Integer.parseInt(
			preferences.getString("SampleRate", "44100"));
	}

	public boolean isLogging()
	{
		return preferences.getBoolean("Logging", false);
	}

	/**
	 * Returns the optimal PCM buffer size in bytes. Because of output buffer
	 * stuffing, the input buffer should be bigger, to prevent the overflow.
	 * */
	public int getPcmBufferSize(int sampleRate)
	{
		int pcmInBufferSize = AudioRecord.getMinBufferSize(
			sampleRate,
			AudioFormat.CHANNEL_IN_MONO, // DON'T CHANGE!
			AudioFormat.ENCODING_PCM_16BIT); // DON'T CHANGE!

		int pcmOutBufferSize = AudioTrack.getMinBufferSize(
			sampleRate,
			AudioFormat.CHANNEL_OUT_MONO, // DON'T CHANGE!
			AudioFormat.ENCODING_PCM_16BIT); // DON'T CHANGE!

		return Math.max(pcmInBufferSize, pcmOutBufferSize);
	}

	public int getFrameSize(FrameType frameType, int sampleRate)
	{
		// An example for the 44,1 kHz sample rate:
		// - Large frame size = 4096
		// - Default frame size = 2048
		// - Medium frame size = 1024
		// - Small frame size = 512

		final double frameSizeRatio = 1D / (44100D / 2048D); // default ratio
		final double frameTypeRatio = frameType.ratio;

		// Only even frame sizes are required
		int frameSize = (int) (sampleRate * frameSizeRatio * frameTypeRatio);
		if (frameSize % 2 != 0) frameSize++;

		return frameSize;
	}

	public int getHopSize(FrameType frameType, int sampleRate)
	{
		// The hop size for a Hann window is 1/4 of the frame size:
		return getFrameSize(frameType, sampleRate) / 4;
	}
}
