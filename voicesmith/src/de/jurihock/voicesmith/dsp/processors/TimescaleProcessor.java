/*
 * Voicesmith <http://voicesmith.jurihock.de/>
 *
 * Copyright (c) 2011-2014 Juergen Hock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.jurihock.voicesmith.dsp.processors;

import static de.jurihock.voicesmith.dsp.Math.PI;
import static de.jurihock.voicesmith.dsp.Math.atan2;
import static de.jurihock.voicesmith.dsp.Math.cos;
import static de.jurihock.voicesmith.dsp.Math.princarg;
import static de.jurihock.voicesmith.dsp.Math.sin;
import static de.jurihock.voicesmith.dsp.Math.sqrt;
import de.jurihock.voicesmith.Disposable;

/**
 * Replaced by the NativeTimescaleProcessor.
 * */
@Deprecated
public final class TimescaleProcessor implements Disposable
{
	private final int			fftSize;
	private final float			timescaleRatio;

	private final float[]		omegaA;
	private final float[]		omegaS;
	private final float[]		prevPhaseA;
	private final float[]		prevPhaseS;

	public TimescaleProcessor(int frameSize, int analysisHopSize, int synthesisHopSize)
	{
		fftSize = frameSize / 2;
		timescaleRatio = (float) synthesisHopSize / (float) analysisHopSize;

		omegaA = new float[fftSize];
		omegaS = new float[fftSize];
		prevPhaseA = new float[fftSize];
		prevPhaseS = new float[fftSize];

		for (int i = 0; i < fftSize; i++)
		{
			omegaA[i] = 2 * PI * (i / (float) frameSize) // not fftSize!
				* (float) analysisHopSize;

			omegaS[i] = 2 * PI * (i / (float) frameSize) // not fftSize!
				* (float) synthesisHopSize;
		}
	}

	public void processFrame(float[] frame)
	{
		if (timescaleRatio == 1)
			return;

		float re, im, abs;
		float nextPhaseA, nextPhaseS;
		float phaseDeltaA, phaseDeltaS;

		for (int i = 1; i < fftSize; i++)
		{
			// Get source Re and Im parts
			re = frame[2 * i];
			im = frame[2 * i + 1];

			// Compute source phase
			nextPhaseA = atan2(im, re);

			if (timescaleRatio < 2)
			{
				// Compute phase deltas
				phaseDeltaA = princarg(nextPhaseA - (prevPhaseA[i] + omegaA[i]));
				phaseDeltaS = phaseDeltaA * timescaleRatio;
	
				// Compute destination phase
				nextPhaseS = princarg((prevPhaseS[i] + omegaS[i]) + phaseDeltaS);
	
				// Save computed phase values
				prevPhaseA[i] = nextPhaseA;
				prevPhaseS[i] = nextPhaseS;
			}
			else
			{
				// Compute destination phase
				nextPhaseS = princarg(nextPhaseA * 2);
			}

			// Compute destination Re and Im parts
			abs = sqrt(re * re + im * im);
			re = abs * cos(nextPhaseS);
			im = abs * sin(nextPhaseS);

			// Save new values
			frame[2 * i] = re;
			frame[2 * i + 1] = im;
		}
	}

	public void dispose()
	{
	}
}
