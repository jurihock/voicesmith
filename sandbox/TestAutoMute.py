from numpy import *
import matplotlib.pyplot as plot
import WAV as wav
from VAD import vad
import STFT as stft

# Load sample input
x, sr = wav.read("wav/x1.wav")

# Set STFT/VAD parameters
winSize = 1024*2      # in samples
hopSize = winSize/4   # in samples
hangoverTime = 200e-3 # in seconds

# Estimate silent frames
vadFlags = vad(x, winSize, hopSize, sr, hangoverTime)

# Get STFT frames
frames = stft.stft(x, winSize, hopSize)

# Disable silent STFT frames
for n in range(len(frames)):
    frames[n] *= vadFlags[n]

# Build output signal
y = stft.istft(frames, winSize, hopSize)

# Plot results
plot.figure()
plot.plot(y, "b")
plot.plot(range(0, len(y)-winSize+1, hopSize), vadFlags/max(vadFlags)*max(y), "r")
plot.show()

# Write out results
wav.write(x, sr, "wav/automute_input.wav")
wav.write(y, sr, "wav/automute_output.wav")