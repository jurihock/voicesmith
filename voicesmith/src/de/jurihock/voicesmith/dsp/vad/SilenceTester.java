package de.jurihock.voicesmith.dsp.vad;

import de.jurihock.voicesmith.Utils;

public final class SilenceTester
{
	private final RMS				rms;
	private final SchmittTrigger	hysteresis;

	private boolean					isSignalSilent	= false;

	public SilenceTester()
	{
		rms = new RMS(10);
		hysteresis = new SchmittTrigger(1000, 1000000);
	}

	public void testFrame(short[] frame)
	{
		for (int i = 0; i < frame.length; i++)
		{
			long value = frame[i];
			long meanValue = rms.getMeanValue(value);
			boolean isSignalSilent = hysteresis.isLow(meanValue);

			if (this.isSignalSilent != isSignalSilent)
			{
				this.isSignalSilent = isSignalSilent;
				Utils.log("SilenceTester: %s, %s, %s",
					isSignalSilent, meanValue, value);
			}
		}
	}
}
