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

import android.content.Context;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.dsp.LuenbergerObserver;

import static de.jurihock.voicesmith.dsp.Math.mean;

public final class OffsetProcessor
{
    private final boolean isEnabled;

    private final float[] offsetObserverGain = new float[] {0.025F, 0F};

    private final LuenbergerObserver offsetObserver;

    public OffsetProcessor(Context context)
    {
        this(new Preferences(context).isCorrectOffsetOn());
    }

    public OffsetProcessor(boolean enable)
    {
        isEnabled = enable;

        offsetObserver = new LuenbergerObserver(0, 0, offsetObserverGain);
    }

    public void processFrame(short[] frame)
    {
        if (!isEnabled) return;

        short currentOffset = mean(frame, 0, frame.length);
        currentOffset = (short)offsetObserver.smooth(currentOffset);

        for (int i = 0; i < frame.length; i++)
        {
            frame[i] -= currentOffset;
        }
    }
}
