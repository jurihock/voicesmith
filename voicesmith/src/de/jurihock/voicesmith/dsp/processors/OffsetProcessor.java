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
        this(new Preferences(context).isCorrectOffset());
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
