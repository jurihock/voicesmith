package de.jurihock.voicesmith;

/**
 * Altered Auditory Feedback modes.
 * */
public enum AAF
{
	/**
	 * Frequency-shifted Auditory Feedback
	 * */
	FAF,

	/**
	 * Delayed Auditory Feedback
	 * */
	DAF,

	/**
	 * Delayed Auditory Feedback (min. possible delay)
	 * */
	FastDAF;

	private static final AAF[] aafValues = AAF.values();

	public static int count()
	{
		return aafValues.length;
	}

	public static AAF valueOf(int aafIndex)
	{
		return aafValues[aafIndex];
	}
}