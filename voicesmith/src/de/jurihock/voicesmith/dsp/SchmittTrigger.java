package de.jurihock.voicesmith.dsp;

public final class SchmittTrigger
{
    private boolean state;
    private float value;

    private final float lowThreshold;
    private final float highThreshold;

    public SchmittTrigger(boolean state, float value, float lowThreshold, float highThreshold)
    {
        this.state = state;
        this.value = value;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
    }

    public boolean state(float newValue)
    {
        if((newValue > value) && (newValue > highThreshold))
        {
            state = true;
        }
        else if((newValue < value) && (newValue < lowThreshold))
        {
            state = false;
        }

        value = newValue;

        return state;
    }
}
