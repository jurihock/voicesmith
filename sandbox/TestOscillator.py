from numpy import *
import WAV as wav
from Oscillator import *

# Load sample input
[x, sr] = wav.read('wav/tomsawyer.wav')

# Make cos wave
osc = Oscillator(sr, 500.0)
f = osc.getBuffer(len(x))

# Shift frequency by f Hz
y = multiply(x, f)

# Write out results
wav.write(x, sr, "wav/freqshift_input.wav")
wav.write(y, sr, "wav/freqshift_output.wav")
