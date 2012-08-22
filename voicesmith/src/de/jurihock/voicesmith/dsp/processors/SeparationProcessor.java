package de.jurihock.voicesmith.dsp.processors;

import static de.jurihock.voicesmith.dsp.Math.PI;
import static de.jurihock.voicesmith.dsp.Math.abs;
import static de.jurihock.voicesmith.dsp.Math.arg;
import static de.jurihock.voicesmith.dsp.Math.imag;
import static de.jurihock.voicesmith.dsp.Math.princarg;
import static de.jurihock.voicesmith.dsp.Math.real;

public final class SeparationProcessor
{
	private final int		fftSize;
	private final float[]	omega;
	private final float[]	prevPhase1;
	private final float[]	prevPhase2;

	public SeparationProcessor(int frameSize, int hopSize)
	{
		fftSize = frameSize / 2;
		omega = new float[fftSize];
		prevPhase1 = new float[fftSize];
		prevPhase2 = new float[fftSize];

		for (int i = 0; i < fftSize; i++)
		{
			omega[i] = 2 * PI * (i / (float) frameSize) // not fftSize!
				* (float) hopSize;
		}
	}

	public void processFrame(float[] frame)
	{
		final int fftSize = frame.length / 2;
		float re, im, abs, nextPhase, phaseDelta;

		for (int i = 1; i < fftSize; i++)
		{
			// Get source Re and Im parts
			re = frame[2 * i];
			im = frame[2 * i + 1];
			abs = abs(re, im);

			// Invert phase value
			nextPhase = arg(re, im);

			// Compute phase delta
			phaseDelta = princarg(nextPhase - 2F * prevPhase1[i] + prevPhase2[i]);
			
			// Disable some frequency components
			if(Math.abs(phaseDelta) > 0.05F * omega[i])
			{
				abs = 0;
				nextPhase = 0;
			}
			
			// Save phase values
			prevPhase2[i] = prevPhase1[i];
			prevPhase1[i] = nextPhase;

			// Compute destination Re and Im parts
			frame[2 * i] = real(abs, nextPhase);
			frame[2 * i + 1] = imag(abs, nextPhase);
		}
	}
}