package de.jurihock.voicesmith.dsp.processors;

import android.content.Context;
import de.jurihock.voicesmith.Preferences;

import static de.jurihock.voicesmith.dsp.Math.ceil;
import static de.jurihock.voicesmith.dsp.Math.rms;
import static de.jurihock.voicesmith.dsp.Math.mean;
import static de.jurihock.voicesmith.dsp.Math.min;
import static de.jurihock.voicesmith.dsp.Math.rms2dbfs;
import static de.jurihock.voicesmith.dsp.Math.round;

public final class VadProcessor
{
    private final int sampleRate;

	private final float windowTimeInterval = 20e-3F;
	private final int windowSize;

	private final float[] meanObserverGain = new float[] {0.025F, 0F};
	private final float[] energyObserverGain = new float[] {0.3F, 0.02F};

	private final LuenbergerObserver meanObserver;
	private final LuenbergerObserver energyObserver;
	private final SchmittTrigger trigger;

    private final float hangoverMaxDuration;
    private float hangoverDuration;

    private final boolean isEnabled;

    public VadProcessor(int sampleRate, Context context)
    {
        this(sampleRate,
            new Preferences(context).getAutoMuteHighThreshold(),
            new Preferences(context).getAutoMuteLowThreshold(),
            new Preferences(context).getAutoMuteHangover(),
            new Preferences(context).isAutoMute());
    }

	public VadProcessor(int sampleRate, int lowThreshold, int highThreshold, int hangover, boolean enable)
	{
        this.sampleRate = sampleRate;

		windowSize = round(sampleRate * windowTimeInterval);

		final float initialDbfs = (lowThreshold + highThreshold) / 2F;

		meanObserver = new LuenbergerObserver(0, 0, meanObserverGain);
		energyObserver = new LuenbergerObserver(initialDbfs, 0, energyObserverGain);
		trigger = new SchmittTrigger(false, initialDbfs, lowThreshold, highThreshold);

        hangoverMaxDuration = hangover;
        hangoverDuration = 0;

        isEnabled = enable;
	}

	public void processFrame(short[] frame)
	{
        if(!isEnabled) return;

		final int windowCount = frame.length / windowSize;
		final int adaptedWindowSize = windowCount > 0
                ? (int)ceil((float)frame.length / (float)windowCount)
                : frame.length;
        final float windowDuration = (float)adaptedWindowSize / (float)sampleRate;

		for (int i = 0; i < windowCount; i++)
		{
			final int windowOffset = i * adaptedWindowSize;
			processFrameInternal(frame, windowOffset, adaptedWindowSize, windowDuration);
		}
	}

	private void processFrameInternal(short[] frame, int offset, int length, float windowDuration)
	{
		short currentMean = mean(frame, offset, length);
		float currentRms = rms(frame, offset, length, currentMean);
		float currentDbfs = rms2dbfs(currentRms);

		currentMean = (short)meanObserver.smooth(currentMean);
		currentDbfs = energyObserver.smooth(currentDbfs);
		boolean currentState = trigger.state(currentDbfs);

        if(hangoverMaxDuration > 0)
        {
            if(!currentState)
            {
                hangoverDuration = min(hangoverMaxDuration, hangoverDuration + windowDuration);
                currentState = hangoverDuration < hangoverMaxDuration;
            }
            else
            {
                hangoverDuration = 0;
            }
        }

		if(!currentState)
		{
			for (int i = offset; i < length; i++)
			{
				frame[i] = currentMean;
			}
		}
	}

	private final class LuenbergerObserver
	{
		private float value;
		private float velocity;

		private final float[] gain;

		public LuenbergerObserver(float value, float velocity, float[] gain)
		{
			this.value = value;
			this.velocity = velocity;
			this.gain = gain;
		}

		private float predict()
		{
			return value + velocity;
		}

		private float correct(float newValue)
		{
			final float prediction = predict();
			final float error = newValue - value;

			value = prediction + gain[0] * error;
			velocity = velocity + gain[1] * error;

			return value;
		}

		public float smooth(float newValue)
		{
			correct(newValue);
			return predict();
		}
	}

	private final class SchmittTrigger
	{
		private boolean state;
		private float value;

		private final float lowThreshold;
		private final float highThreshold;

		public SchmittTrigger(boolean state, float value, float lowThreshold, float highThreshold)
		{
			this.state = state;
			this.value = value;
			this.lowThreshold = lowThreshold;
			this.highThreshold = highThreshold;
		}

		public boolean state(float newValue)
		{
			if((newValue > value) && (newValue > highThreshold))
			{
				state = true;
			}
			else if((newValue < value) && (newValue < lowThreshold))
			{
				state = false;
			}

			value = newValue;

			return state;
		}
	}
}
