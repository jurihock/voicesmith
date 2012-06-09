/*******************************************************************************
 * src/de/jurihock/voicesmith/dsp/dafx/TimescaleProcessor.java
 * is part of the Voicesmith project
 * <http://voicesmith.jurihock.de>
 * 
 * Copyright (C) 2011-2012 Juergen Hock
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
 ******************************************************************************/

package de.jurihock.voicesmith.dsp.dafx;

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
	private final float[]		omega;
	private final float[]		prevPhaseA;
	private final float[]		prevPhaseS;
	private final float			phaseScaleRatio;

	public TimescaleProcessor(int frameSize, int analysisHopSize, int synthesisHopSize)
	{
		omega = new float[frameSize];
		for (int i = 0; i < frameSize; i++)
		{
			// TODO: 2pi k/N?
			omega[i] = 2f * PI * (i / (float) frameSize)
				* (float) analysisHopSize;
		}

		prevPhaseA = new float[frameSize];
		prevPhaseS = new float[frameSize];

		phaseScaleRatio = (float) synthesisHopSize / (float) analysisHopSize;
	}

	public void processFrame(float[] frame)
	{
		final int fftSize = frame.length / 2;

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

			// Compute phase deltas
			phaseDeltaA = omega[i]
				+ princarg(nextPhaseA - prevPhaseA[i] - omega[i]);
			phaseDeltaS = phaseDeltaA * phaseScaleRatio;

			// Compute destination phase
			nextPhaseS = princarg(prevPhaseS[i] + phaseDeltaS);

			// Save computed phase values
			prevPhaseA[i] = nextPhaseA;
			prevPhaseS[i] = nextPhaseS;

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
