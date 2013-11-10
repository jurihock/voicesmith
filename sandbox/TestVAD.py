from numpy import *
import matplotlib.pyplot as plot
import WAV as wav
from VAD import vad

# Load sample input
x, sr = wav.read("wav/test/test_bh_raw.wav")

# Set VAD parameters
winSize = int(sr*20e-3)         # in samples (fs*s)
hopSize = winSize               # in samples
smoothingGain = [0.3, 0.001]    # small numbers
triggerThresholds = [-25, -20]  # in dBFS

# Estimate silent frames
vadFlags = vad(x, winSize, hopSize, smoothingGain, triggerThresholds)

# Plot results
plot.figure()
plot.plot(x, "b")
#plot.plot(range(0, len(x)-winSize, hopSize), vadFlags*max(x), "r")
plot.plot(range(0, len(x)-winSize, hopSize), vadFlags[:,1], "r", linewidth=2) # TEST
plot.show()