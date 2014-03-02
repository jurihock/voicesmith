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

import android.content.Context;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.Utils;

import static de.jurihock.voicesmith.dsp.Math.abs;
import static de.jurihock.voicesmith.dsp.Math.pow;

public final class DenoiseProcessor
{
    private final int sampleRate;

    private final boolean isSpectralNoiseGateOn;
    private final boolean isBandpassFilterOn;

    private final float ngCoeff;

    private final float bpLowerFreq;
    private final float bpUpperFreq;

    public DenoiseProcessor(int sampleRate, Context context)
    {
        this.sampleRate = sampleRate;

        Preferences preferences = new Preferences(context);

        isSpectralNoiseGateOn = preferences.isSpectralNoiseGateOn();
        isBandpassFilterOn = preferences.isBandpassFilterOn();

        // Just precompute the required values
        ngCoeff = pow(10F, -preferences.getNoiseGateCoeffExponent());
        bpLowerFreq = 2F * preferences.getBandpassLowerFreq() / (float)sampleRate;
        bpUpperFreq = 2F * preferences.getBandpassUpperFreq() / (float)sampleRate;

        new Utils(context).log("Spectral noise gate coeff is %s.",
                Float.toString(ngCoeff));
        new Utils(context).log("Bandpass freqs are %s and %s.",
            Integer.toString(preferences.getBandpassLowerFreq()),
            Integer.toString(preferences.getBandpassUpperFreq()));
    }

	public void processFrame(float[] frame)
	{
        if (!isSpectralNoiseGateOn && !isBandpassFilterOn) return;

		final int fftSize = frame.length / 2;

        final float coeff = ngCoeff;

        final int start = (int)(fftSize * bpLowerFreq);
        final int end = (int)(fftSize * bpUpperFreq);

		float re, im, abs, r;

        boolean isIndexInBand = true;

		for (int i = 1; i < fftSize; i++)
		{
			// Get source Re and Im parts
			re = frame[2 * i];
			im = frame[2 * i + 1];
			abs = abs(re, im);
            r = 1;

            // Perform bandpass filtering, if enabled
            if (isBandpassFilterOn)
            {
                isIndexInBand = (i >= start) && (i <= end);

                if (!isIndexInBand)
                {
                    r = 0;
                }
            }

            // Compute spectral scaling factor, if enabled
            if (isSpectralNoiseGateOn && isIndexInBand)
            {
                r = noiseGate(abs / fftSize, coeff);
            }

			// Compute destination Re and Im parts
			frame[2 * i] = re * r;
			frame[2 * i + 1] = im * r;
		}
	}

	private static float noiseGate(float value, float coeff)
	{
        return value / (value + coeff);
	}
}
