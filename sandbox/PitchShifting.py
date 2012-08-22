from numpy import *
import matplotlib.pyplot as plot
import STFT as stft
import WAV as wav

#==============================================================================
# General constants
SR = 44100
windowSize = 512
hopSize = windowSize / 4
signalSize = windowSize*5
#==============================================================================

#==============================================================================
# Input signal
t = arange(0, 1, 1.0/SR)
f = 5000
x = sin(2*pi*f*t) 
x = x[0:signalSize]
#==============================================================================

#==============================================================================
frames = stft.stft(x, windowSize, hopSize)

phases = zeros(len(frames[0]))

tau = 1
for n in range(0, len(frames)):

    frameIn = frames[n]
    frameOut = zeros(len(frameIn),dtype=complex)

    maxA = (len(frameIn)-1)/2
    for a in range(0, maxA):

        b = a * 2 + 0.5;
        phases[a] += (2*pi*(b-a)/windowSize)*hopSize
        frameOut[b] = frameIn[a] * exp(1j*phases[a])

    dummy = range(0, len(frames[n]))
    plot.figure()
    plot.subplot(211)
    plot.title('frameIn')
    plot.stem(dummy, abs(frameIn))
    plot.subplot(212)
    plot.title('frameOut')
    plot.stem(dummy, abs(frameOut))
    plot.show()

    frames[n] = frameOut;

y = stft.istft(frames, windowSize, hopSize)

#==============================================================================

#==============================================================================
plot.figure()
plot.subplot(211)
plot.title('x')
plot.plot(x)
#plot.specgram(x)
plot.subplot(212)
plot.title('y')
plot.plot(y)
#plot.specgram(y)
plot.show()
##==============================================================================