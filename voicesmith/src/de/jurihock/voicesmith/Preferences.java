/*******************************************************************************
 * src/de/jurihock/voicesmith/Preferences.java
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

package de.jurihock.voicesmith;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.preference.PreferenceManager;

public final class Preferences
{
	// TODO: Try different audio sources
	public static final int			PCM_IN_SOURCE	= MediaRecorder.AudioSource.MIC;
	// public static final int PCM_IN_SOURCE =
	// MediaRecorder.AudioSource.VOICE_CALL;
	// public static final int PCM_IN_SOURCE =
	// MediaRecorder.AudioSource.VOICE_DOWNLINK;
	// public static final int PCM_IN_SOURCE =
	// MediaRecorder.AudioSource.VOICE_UPLINK;
	public static final int			PCM_OUT_SOURCE	= AudioManager.STREAM_MUSIC;
	// public static final int PCM_OUT_SOURCE = AudioManager.STREAM_VOICE_CALL;

	private final SharedPreferences	preferences;

	public Preferences(Context context)
	{
		preferences = PreferenceManager
			.getDefaultSharedPreferences(context);

		PreferenceManager.setDefaultValues(
			context, R.xml.preferences, false);
	}

	public void reset()
	{
		preferences.edit().clear().commit();
	}

	public int getVolumeLevel()
	{
		int vl = Integer.parseInt(
			preferences.getString("prefVolumeLevel", "100"));

		return vl;
	}

	public boolean setVolumeLevel(int newVolumeLevel)
	{
		if ((0 <= newVolumeLevel) && (newVolumeLevel <= 100))
		{
			return preferences.edit()
				.putString("prefVolumeLevel",
					Integer.toString(newVolumeLevel))
				.commit();
		}

		return false;
	}

	public boolean isReduceNoise()
	{
		return preferences.getBoolean("prefReduceNoise", true);
	}

	public int getSampleRate()
	{
		int sr = Integer.parseInt(
			preferences.getString("prefSampleRate", "44100"));

		// TODO: Handle system default sample rate
		// if (sr == 0)
		// {
		// sr = AudioTrack
		// .getNativeOutputSampleRate(
		// AudioManager.STREAM_SYSTEM);
		// }

		return sr;
	}

	public enum FrameType
	{
		Large(2),
		Default(1),
		Medium(1D / 2),
		Small(1D / 4);

		public final double	ratio;

		private FrameType(double ratio)
		{
			this.ratio = ratio;
		}
	}

	public int getFrameSize(FrameType frameType)
	{
		// An example for the 44,1 kHz sample rate:
		// - Large frame size = 4096
		// - Default frame size = 2048
		// - Medium frame size = 1024
		// - Small frame size = 512

		final double frameSizeRatio = 1D / (44100D / 2048D); // default ratio
		final double frameTypeRatio = frameType.ratio;

		return (int) (getSampleRate() * frameSizeRatio * frameTypeRatio);
	}

	public int getHopSize(FrameType frameType)
	{
		return getFrameSize(frameType) / 4;
	}

	/**
	 * Returns the optimal PCM buffer size. Because of output buffer stuffing,
	 * the input buffer should be bigger, to prevent the overflow.
	 * */
	public int getPcmBufferSize()
	{
		int sampleRate = getSampleRate();

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
}
