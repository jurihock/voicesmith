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
