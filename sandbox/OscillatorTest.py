from numpy import *
from Oscillator import *
#import matplotlib.pyplot as plot

import WAV as wav

[input, sr] = wav.read('wav/x1.wav')

oscillator = Oscillator(sr, 1000.0)

wave = oscillator.getBuffer(len(input))

output = multiply(input, wave)

#wav.sound(input, sr, "/tmp/input.wav")
wav.sound(output, sr, "/tmp/output.wav")
