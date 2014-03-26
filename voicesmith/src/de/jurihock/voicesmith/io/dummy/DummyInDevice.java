package de.jurihock.voicesmith.io.dummy;

import android.content.Context;
import de.jurihock.voicesmith.io.AudioDevice;

public final class DummyInDevice  extends AudioDevice
{
    public DummyInDevice(Context context)
    {
        super(context);
    }

    @Override
    public int read(short[] buffer, int offset, int count)
    {
        // TODO: Simulate AD conversion timeout

        for (int i = offset; i < count; i++)
        {
            buffer[i] = 0;
        }

        return count;
    }
}
