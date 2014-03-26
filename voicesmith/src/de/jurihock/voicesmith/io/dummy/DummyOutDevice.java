package de.jurihock.voicesmith.io.dummy;

import android.content.Context;
import de.jurihock.voicesmith.io.AudioDevice;

public final class DummyOutDevice extends AudioDevice
{
    public DummyOutDevice(Context context)
    {
        super(context);
    }

    @Override
    public int write(short[] buffer, int offset, int count)
    {
        // TODO: Simulate DA conversion timeout

        return count;
    }
}
