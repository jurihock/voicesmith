package de.jurihock.voicesmith.dsp.processors;

import static de.jurihock.voicesmith.dsp.Math.sqrt;
import static de.jurihock.voicesmith.dsp.Math.max;
import static de.jurihock.voicesmith.dsp.Math.min;
import static de.jurihock.voicesmith.dsp.Math.log10;
import static de.jurihock.voicesmith.dsp.Math.round;

public final class VadProcessor
{
	private final float windowTimeInterval = 20e-3F;
	private final int windowSize;

	private final float[] meanObserverGain = new float[] {0.01F, 0.001F};
	private final float[] energyObserverGain = new float[] {0.3F, 0.001F};

	private final LuenbergerObserver meanObserver;
	private final LuenbergerObserver energyObserver;
	private final SchmittTrigger trigger;

	public VadProcessor(int sampleRate, int lowThreshold, int highThreshold)
	{
		windowSize = round(sampleRate * windowTimeInterval);

		final float initialDbfs = (lowThreshold + highThreshold) / 2F;

		meanObserver = new LuenbergerObserver(0, 0, meanObserverGain);
		energyObserver = new LuenbergerObserver(initialDbfs, 0, energyObserverGain);
		trigger = new SchmittTrigger(false, initialDbfs, lowThreshold, highThreshold);
	}

	public void processFrame(short[] frame)
	{
		final int windowCount = frame.length / windowSize;
		final int adaptedWindowSize = frame.length / windowCount;

		for (int i = 0; i < windowCount; i++)
		{
			final int windowOffset = i * adaptedWindowSize;
			processFrameInternal(frame, windowOffset, adaptedWindowSize);
		}
	}

	private void processFrameInternal(short[] frame, int offset, int length)
	{
		short currentMean = mean(frame, offset, length);
		float currentRms = rms(frame, offset, length, currentMean);
		float currentDbfs = rms2dbfs(currentRms);

		currentMean = (short)meanObserver.smooth(currentMean);
		currentDbfs = energyObserver.smooth(currentDbfs);
		boolean currentState = trigger.state(currentDbfs);

		if(!currentState)
		{
			for (int i = offset; i < length; i++)
			{
				frame[i] = currentMean;
			}
		}
	}

	private static short mean(short[] frame, int offset, int length)
	{
		long mean = 0;

		for (int i = offset; i < length; i++)
		{
			mean += frame[i];
		}

		mean /= length;

		return (short)mean;
	}

	private static float rms(short[] frame, int offset, int length, short mean)
	{
		float rms = 0;

		for (int i = offset; i < length; i++)
		{
			float value = (frame[i] - mean) / 32767F;
			rms += value * value;
		}

		rms = sqrt(rms / length);

		return rms;
	}

	private static float rms2dbfs(float value)
	{
		value = min(max(value,1e-10F),1F);

		return 10F*log10(value);
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

		public float predict()
		{
			return value + velocity;
		}

		public float correct(float newValue)
		{
			final float prediction = predict();
			final float error = newValue - prediction;

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
