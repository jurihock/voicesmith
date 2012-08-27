package de.jurihock.voicesmith.dsp.vad;

import static de.jurihock.voicesmith.dsp.Math.sqrt;

/**
 * Computes the Root Mean Square (RMS) value.
 * */
public final class RMS
{
	private final int		rmsBufferSize;
	private final long[]	rmsBuffer;
	private int				rmsBufferCursor	= -1;
	private long			rmsBufferSum	= 0;

	public RMS(int bufferSize)
	{
		if (bufferSize < 1)
		{
			throw new IllegalArgumentException(
				"The RMS buffer must contain at least one value!");
		}

		rmsBufferSize = bufferSize;
		rmsBuffer = new long[rmsBufferSize];
	}

	public int getBufferSize()
	{
		return rmsBufferSize;
	}

	public long getMeanValue(long value)
	{
		// Rotate RMS buffer by one position --
		// set the corresponding cursor
		rmsBufferCursor++;
		if (rmsBufferCursor >= rmsBufferSize) rmsBufferCursor = 0;

		// Subtract the oldest value
		rmsBufferSum -= rmsBuffer[rmsBufferCursor];

		// Add the current value
		rmsBuffer[rmsBufferCursor] = value * value;
		rmsBufferSum += rmsBuffer[rmsBufferCursor];

		// $ \sum x^2 $
		// return rmsBufferSum;

		// $ 1/N \sum x^2 $
		// return (long) (rmsBufferSum / (double) rmsBufferSize);

		// $ \sqrt{ 1/N \sum x^2 } $
		// TODO: float or double?
		return (long) sqrt((float) rmsBufferSum / (float) rmsBufferSize);
	}
}
