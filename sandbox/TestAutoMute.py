from numpy import *
import matplotlib.pyplot as plot
import WAV as wav
from VAD import vad, mute

filename = "tomsawyer.wav"

# Load sample input
x, sr = wav.read("wav/" + filename)

# Set VAD parameters
winSize = int(sr*20e-3)         # in samples (fs*s)
hopSize = winSize               # in samples
smoothingGain = [0.3, 0.02]     # small numbers
triggerThresholds = [-25, -20]  # in dBFS

# Estimate silent frames
vadFlags = vad(x, winSize, hopSize, smoothingGain, triggerThresholds)

# Mute silent frames
y = mute(x, winSize, hopSize, vadFlags)

# Plot results
plot.figure()
plot.plot(x, "b")
plot.plot(y, "r")
plot.plot(range(0, len(y)-winSize, hopSize), vadFlags[:,1], "g--")
plot.legend(("Input signal", "Output signal", "Signal offset"))
plot.show()

# Write out results
wav.write(x, sr, "wav/automute_input.wav")
wav.write(y, sr, "wav/automute_output.wav")

plot.savefig("wav/test/Results/" + filename + ".pdf", dpi=600)
wav.write(y, sr, "wav/test/Results/" + filename)