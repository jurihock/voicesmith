package de.jurihock.voicesmith.dsp.vad;

/**
 * Computes the Root Mean Square (RMS) value, i.e. only the Parseval's sum of
 * squares, without square root or division by N.
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

		return rmsBufferSum;
	}
}
