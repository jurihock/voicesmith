package de.jurihock.voicesmith.dsp.vad;

/**
 * Dynamical energy-based Voice Activity Detector (VAD).
 * */
public final class DynamicalVAD
{
	private static final double	ANALYZE_TIME_SLOT	= 0.01; // 10ms
	private static final int	HANGOVER_THRESHOLD	= 4;	// 4 frames

	private final int			analyzeFrameSize;

	private double				delta;
	private double				lambda;

	private double				threshold;
	private int					hangover;
	private double				minEnergy;
	private double				maxEnergy;

	public DynamicalVAD(int sampleRate)
	{
		analyzeFrameSize = (int) (sampleRate * ANALYZE_TIME_SLOT);
	}

	public void analyzeFrame(short[] frame)
	{
		final int analyzeFrameCount = frame.length / analyzeFrameSize;

		for (int i = 0; i < analyzeFrameCount; i++)
		{
			final double currentEnergy = getRmsValue(
				frame, i * analyzeFrameSize, analyzeFrameSize);

			if (currentEnergy > maxEnergy)
			{
				maxEnergy = currentEnergy;
			}

			if (currentEnergy < minEnergy)
			{
				minEnergy = currentEnergy;
				delta = 1;
			}

			if (minEnergy == 0)
			{
				minEnergy = getMinValue(
					frame, i * analyzeFrameSize, analyzeFrameSize);
				delta = 1;
			}

			delta *= 1.0001;
			lambda = (maxEnergy - minEnergy) / maxEnergy;
			threshold = (1 - lambda) * maxEnergy + lambda * minEnergy;

			if (currentEnergy > threshold)
			{
				// VOICED!

				hangover = 0;
			}
			else if (hangover == HANGOVER_THRESHOLD)
			{
				// NOISE!
			}
			else
			{
				// VOICED!

				hangover++;
			}

			minEnergy *= delta;
		}
	}

	private static double getRmsValue(short[] frame, int offset, int count)
	{
		long sum = 0;

		for (int i = offset; i < offset + count; i++)
		{
			sum += frame[i] * frame[i];
		}

		return Math.sqrt(sum / (double) count);
	}

	private static short getMinValue(short[] frame, int offset, int count)
	{
		short min = 0;

		for (int i = offset; i < offset + count; i++)
		{
			if (frame[i] < min) min = frame[i];
		}

		return min;
	}
}
