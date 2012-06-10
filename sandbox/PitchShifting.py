from numpy import *
import matplotlib.pyplot as plot
import STFT as stft
import WAV as wav

#==============================================================================
# General constants
SR = 44100
windowSize = 512
hopSize = windowSize / 4
signalSize = windowSize*10
#==============================================================================

#==============================================================================
# Input signal
t = arange(0, 1, 1.0/SR)
f = 1000
x = sin(2*pi*f*t) 
x = x[0:signalSize]
#==============================================================================

#==============================================================================
frames = stft.stft(x, windowSize, hopSize)

tau = 1
for n in range(0, len(frames)):

    frame = frames[n]

    srcIdx = arange(1, len(frame)/tau-1)
    dstIdx = srcIdx[:]*tau

    srcFreqs = abs(frame[srcIdx])
    dstFreqs = abs(frame[dstIdx])

    dstAbs = maximum(srcFreqs, dstFreqs)
    dstArg = angle(frame[dstIdx])
    frame[dstIdx] = dstAbs*cos(dstArg) + 1j*dstAbs*sin(dstArg)
    frame[srcIdx] = 0

y = stft.istft(frames, windowSize, hopSize)

#==============================================================================

#==============================================================================
#plot.figure()
#plot.subplot(211)
#plot.title('x')
#plot.plot(x)
##plot.specgram(x)
#plot.subplot(212)
#plot.title('y')
#plot.plot(y)
##plot.specgram(y)
#plot.show()
##==============================================================================