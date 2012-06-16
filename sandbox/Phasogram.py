from numpy import *
import matplotlib.pyplot as plot
import STFT as stft
import WAV as wav

def princarg(phase):
    return mod(phase+pi, 2*pi) - pi

#==============================================================================
# General constants
SR = 44100
windowSize = 512
signalSize = windowSize*2
#==============================================================================

#==============================================================================
# Input signal
t = arange(0, 1, 1.0/SR)
f = 1000
x = sin(2*pi*f*t)
for i in range(2,6): x += sin(2*pi*(f*i)*t)
x = x[0:signalSize]
#==============================================================================

#x, SR = wav.read("wav/la.wav")
#x = x[2000:5000]

phasogram = stft.stft(x, windowSize, 1)

phasogram = angle(phasogram)
#phasogram = log(abs(phasogram))
phasogram = fft.irfft(phasogram)

# Frequency bin of the input signal
k = len(phasogram) * f / (SR/2)

#==============================================================================
#plot.figure()
#r=range(0, len(phasogram)) # display range
#plot.subplot(211)
#plot.title('Signalabschnitt')
#plot.ylabel('[-1,1]')
#plot.plot(r, x[r])
#plot.subplot(212)
#plot.title('Phasogram')
#plot.ylabel('[-pi,pi]')
#plot.plot(r, phasogram[r,k])
#plot.show()
#==============================================================================

#==============================================================================
plot.figure()
plot.imshow(phasogram.transpose(), origin='lower', cmap='flag')
plot.show()
#==============================================================================