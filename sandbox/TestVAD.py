from numpy import *
import matplotlib.pyplot as plot
import WAV as wav
from VAD import vad

# Load sample input
x, sampleRate = wav.read("wav/tomsawyer.wav")

# Set VAD parameters
winSize = 1024*2      # in samples
hopSize = winSize/4   # in samples
noiseEnergy = 0.05    # in amplitude^2 TODO: dB --> RMS
hangoverTime = 200e-3 # in seconds

# Estimate silent frames
vadFlags = vad(x, winSize, hopSize, sampleRate, noiseEnergy, hangoverTime)

# Plot results
plot.figure()
plot.plot(x, "b")
plot.plot(range(0, len(x)-winSize, hopSize), vadFlags/max(vadFlags)*max(x), "r")
plot.show()