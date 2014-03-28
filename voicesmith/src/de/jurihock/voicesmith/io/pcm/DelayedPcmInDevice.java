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

package de.jurihock.voicesmith.io.pcm;

import java.io.IOException;
import java.util.Arrays;

import android.content.Context;
import de.jurihock.voicesmith.audio.HeadsetMode;

public final class DelayedPcmInDevice extends PcmInDevice
{
	private double	delayTime		= 0;
	private int		delaySamples	= 0;
	private int		remainingZeros	= 0;

	public DelayedPcmInDevice(Context context, HeadsetMode headsetMode)
		throws IOException
	{
		super(context, headsetMode);
	}

	public DelayedPcmInDevice(Context context, int sampleRate)
		throws IOException
	{
		super(context, sampleRate);
	}

	public DelayedPcmInDevice(Context context) throws IOException
	{
		super(context);
	}

	public double getDelay()
	{
		return delayTime;
	}

	public void setDelay(double seconds)
	{
		delayTime = seconds;

		final int oldDelay = delaySamples - remainingZeros;
		if (oldDelay > 0)
		{
			final short[] oldSamples = new short[oldDelay];
			read(oldSamples);
		}

		delaySamples = (int) (delayTime * getSampleRate());
		remainingZeros = delaySamples;
	}

	@Override
	public int read(short[] buffer, int offset, int count)
	{
		if (getDelay() == 0 || remainingZeros == 0)
		{
			return super.read(buffer, offset, count);
		}

		if (remainingZeros >= count)
		{
			try
			{
				Arrays.fill(buffer, offset, offset + count, (short) 0);

				return count;
			}
			finally
			{
				remainingZeros -= count;
			}
		}
		else
		{
			try
			{
				Arrays.fill(buffer, offset, offset + remainingZeros, (short) 0);

				return super.read(buffer, offset + remainingZeros,
					count - remainingZeros);
			}
			finally
			{
				remainingZeros = 0;
			}
		}
	}
}
