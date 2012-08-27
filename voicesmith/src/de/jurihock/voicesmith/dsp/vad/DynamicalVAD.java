package de.jurihock.voicesmith.dsp.vad;

/**
 * Dynamical energy-based Voice Activity Detector (VAD).
 * */
public final class DynamicalVAD
{
	private static final int ANALYSIS_TIME = 10;
	
	private final RMS rms;
	
	private long threshold;
	private long minEnergy, maxEnergy;

	public DynamicalVAD(int sampleRate)
	{
		final int analysisFrameSize = sampleRate * ANALYSIS_TIME;
		
		rms = new RMS(analysisFrameSize);
	}
	
	public void foo(short[] frame)
	{
	}
}
