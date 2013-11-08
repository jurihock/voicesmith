from numpy import *
import matplotlib.pyplot as plot
import WAV as wav
from VAD import vad, mute

# Load sample input
#x, sr = wav.read("wav/x1.wav")
x, sr = wav.read("wav/test/test_wh_raw.wav")

# Set VAD parameters
winSize = int(sr*20e-3)         # in samples (fs*s)
hopSize = winSize               # in samples
smoothGain = [0.2, 0.001]       # small numbers
triggerThresholds = [-25, -20]  # in dBFS

# Estimate silent frames
vadFlags = vad(x, winSize, hopSize, smoothGain, triggerThresholds)

# Mute silent frames
y = mute(x, winSize, hopSize, vadFlags)

# Plot results
plot.figure()
plot.plot(y, "b")
plot.plot(range(0, len(y)-winSize+1, hopSize), vadFlags*max(y), "r")
plot.show()

# Write out results
wav.write(x, sr, "wav/automute_input.wav")
wav.write(y, sr, "wav/automute_output.wav")