package de.jurihock.voicesmith.dsp.silence;

public final class SchmittTrigger
{
	private final long	lowThreshold;
	private final long	highThreshold;

	private boolean		state	= false;

	/**
	 * LOW state by default.
	 * */
	public SchmittTrigger(long lowThreshold, long highThreshold)
	{
		this(lowThreshold, highThreshold, false);
	}

	public SchmittTrigger(long lowThreshold, long highThreshold, boolean initialState)
	{
		if (lowThreshold > highThreshold)
		{
			throw new IllegalArgumentException(
				"The low threshold must be less or equal than the high threshold!");
		}

		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.state = initialState;
	}

	public boolean isHigh(long value)
	{
		setState(value);
		return state;
	}

	public boolean isLow(long value)
	{
		setState(value);
		return !state;
	}

	/**
	 * Sets trigger state according to the passed value.
	 * */
	private void setState(long value)
	{
		// If curent state is HIGH:
		if (state)
		{
			if (value < lowThreshold) state = false;
		}
		// If curent state is LOW:
		else
		{
			if (value > highThreshold) state = true;
		}
	}
}
