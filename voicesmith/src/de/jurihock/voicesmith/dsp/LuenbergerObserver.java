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