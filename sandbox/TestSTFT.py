from numpy import *
import matplotlib.pyplot as plot
import WAV as wav
import STFT as stft
from Oscillator import *

# Load sample input...
# x, sr = wav.read("wav/tomsawyer.wav")

# or produce cos wave
# sr = 44100.0
# osc = Oscillator(sr, 440.0)
# x = 0.5 * osc.getBuffer(123456)

# Set STFT parameters
winSize = 1024*2      # in samples
hopSize = winSize/4   # in samples

# Build output signal
frames = stft.stft(x, winSize, hopSize)
y = stft.istft(frames, winSize, hopSize)

# Plot x-y difference
n = min(len(x), len(y))
diff = abs(x[0:n] - y[0:n])
plot.figure()
plot.plot(diff, "b")
plot.show()

# Write out results
wav.write(x, sr, "wav/stft_input.wav")
wav.write(y, sr, "wav/stft_output.wav")