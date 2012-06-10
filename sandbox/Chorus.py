from numpy import *
import matplotlib.pyplot as plot
import STFT as stft
import WAV as wav

#==============================================================================
# General constants
SR = 44100
windowSize = 512
signalSize = windowSize*3
#==============================================================================

#==============================================================================
# Input signal
t = arange(0, 1, 1.0/SR)
f = 1000
x = sin(2*pi*f*t)
x = x[0:signalSize]
#==============================================================================

# LFO
f_LFO
lfo = abs(sin(2*pi*f_LFO*t));

bufferIndex = round(lfo*(length(x)-startIdx))+startIdx;

#==============================================================================
plot.figure()
r=range(0, windowSize) # display range
plot.plot(r, x[r])
plot.show()
#==============================================================================