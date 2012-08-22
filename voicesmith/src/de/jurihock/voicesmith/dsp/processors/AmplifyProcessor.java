package de.jurihock.voicesmith.dsp.processors;

import android.content.Context;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.dsp.Math;

public class AmplifyProcessor
{
	private final float	ampFactor;

	public AmplifyProcessor(Context context)
	{
		this(new Preferences(context).getSoundAmplification());
	}

	/**
	 * @param ampDecibel
	 *            Amplification level in [dB].
	 * */
	public AmplifyProcessor(int ampDecibel)
	{
		// http://www.sengpielaudio.com/Rechner-pegelaenderung.htm
		ampFactor = Math.pow(10F, ampDecibel / 20F);
	}

	public void processFrame(short[] frame)
	{
		if (ampFactor == 1) return;

		for (int i = 0; i < frame.length; i++)
		{
			float result = frame[i] * ampFactor;

			if (result > 32767F)
			{
				frame[i] = 32767;
			}
			else if (result < -32768F)
			{
				frame[i] = -32768;
			}
			else
			{
				frame[i] = (short) result;
			}
		}
	}
}
