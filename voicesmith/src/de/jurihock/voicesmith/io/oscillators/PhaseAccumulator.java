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

package de.jurihock.voicesmith.io.oscillators;

import static de.jurihock.voicesmith.dsp.Math.PI;
import android.content.Context;
import de.jurihock.voicesmith.io.AudioDevice;

public abstract class PhaseAccumulator extends AudioDevice
{
	private float	phaseDeviation;
	private float	phaseDivisor;

	public PhaseAccumulator(Context context, int frequency)
	{
		super(context);

		this.setWaveFrequency(frequency);
	}

	public PhaseAccumulator(Context context, int sampleRate, int waveFrequency)
	{
		super(context, sampleRate);

		this.setWaveFrequency(waveFrequency);
	}

	private int	waveFrequency;

	public int getWaveFrequency()
	{
		return waveFrequency;
	}

	private void setWaveFrequency(int waveFrequency)
	{
		this.waveFrequency = waveFrequency;

		phaseDeviation = getWaveFrequency() / getSampleRate();
		phaseDivisor = -phaseDeviation;
	}

	protected float getNextPhase()
	{
		phaseDivisor += phaseDeviation;

		while(phaseDivisor >= 1)
			phaseDivisor -= 1;

		return 2 * PI * phaseDivisor;
	}

	@Override
	public void flush()
	{
		phaseDivisor = -phaseDeviation;
	}
}
