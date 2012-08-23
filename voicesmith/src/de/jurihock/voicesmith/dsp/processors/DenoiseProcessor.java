/*******************************************************************************
 * Voicesmith <http://voicesmith.jurihock.de/>
 * Copyright (c) 2011-2012 Juergen Hock
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

package de.jurihock.voicesmith.dsp.processors;

import static de.jurihock.voicesmith.dsp.Math.abs;
import static de.jurihock.voicesmith.dsp.Math.max;

public final class DenoiseProcessor
{
	public static void processFrame(float[] frame)
	{
		final int fftSize = frame.length / 2;
		float re, im, abs, r;
		
		for (int i = 1; i < fftSize; i++)
		{
			// Get source Re and Im parts
			re = frame[2 * i];
			im = frame[2 * i + 1];
			abs = abs(re, im);

			// Compute scaling factor
			r = noiseGate1(abs / fftSize);

			// Compute destination Re and Im parts
			frame[2 * i] = re * r;
			frame[2 * i + 1] = im * r;
		}
	}

	// Shows best results...
	private static float noiseGate1(float r)
	{
		return r / (r + 0.01F);
	}

	@Deprecated
	private static float noiseGate2(float r)
	{
		return (r * r) / (r + 0.01F);
	}

	@Deprecated
	private static float noiseGate3(float r)
	{
		return max(r, 0.01F);
	}
}
