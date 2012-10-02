from numpy import *
import matplotlib.pyplot as plot

sampleRate = 8000
cosineFrequency = 1000
samplesPerPeriod = sampleRate / cosineFrequency
sample = 0

samples = arange(0, 1, 1.0/sampleRate)

for i in range(len(samples)):

    t = 1.0 * sample / sampleRate
    samples[i] = cos(2*pi*cosineFrequency*t)

    sample = sample + 1
    if (sample >= samplesPerPeriod):
        sample = 0

plot.figure()
plot.plot(samples)
plot.show()