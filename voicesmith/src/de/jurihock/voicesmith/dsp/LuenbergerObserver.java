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

package de.jurihock.voicesmith.dsp;

public final class LuenbergerObserver
{
    private float value;
    private float velocity;

    private final float[] gain;

    public LuenbergerObserver(float value, float velocity, float[] gain)
    {
        this.value = value;
        this.velocity = velocity;
        this.gain = gain;
    }

    private float predict()
    {
        return value + velocity;
    }

    private float correct(float newValue)
    {
        final float prediction = predict();
        final float error = newValue - value;

        value = prediction + gain[0] * error;
        velocity = velocity + gain[1] * error;

        return value;
    }

    public float smooth(float newValue)
    {
        correct(newValue);
        return predict();
    }
}