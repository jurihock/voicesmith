from numpy import *
import matplotlib.pyplot as plot
plot.rc('text', usetex=True)

def princarg(phase):
    return mod(phase+pi, -2*pi) + pi

#==============================================================================
# General constants
SR = 44100
windowSize = 512
signalSize = windowSize*2
#==============================================================================

#==============================================================================
# Window coefficients
window = hanning(windowSize+1)
window = window[0:window.size-1]
#==============================================================================

#==============================================================================
# Input signal
t = arange(0, 1, 1.0/SR)
f = 1000
x = sin(2*pi*f*t) 
x = x[0:signalSize]
#==============================================================================

#==============================================================================
# Phasogram matrix (arg(f), n)
phasogram = zeros((windowSize/2+1, x.size-windowSize)) # (rows, cols)

# Frequency bin of the input signal
k = phasogram.shape[0] * f / (SR/2)
#==============================================================================

#==============================================================================
# STFT
for n in range(0, x.size-windowSize):
    frame = x[n:n+windowSize] * window
    frame = fft.fftshift(frame)
    spec = fft.rfft(frame)

    phasogram[:,n] = angle(spec) # all 
#    phasogram[k,n] = angle(spec)[k] # only k-th bin
#==============================================================================

#==============================================================================
plot.figure()
r=range(0, phasogram.shape[1]) # display range
plot.subplot(211)
plot.title('Signalabschnitt')
plot.ylabel('$[-1,1]$')
plot.plot(r, x[r])
plot.subplot(212)
plot.title('Phasogram')
plot.ylabel('$[-\pi,\pi]$')
plot.plot(r, phasogram[k,r])
plot.show()
#==============================================================================

#==============================================================================
plot.figure()
plot.imshow(phasogram, origin='lower', cmap='gray')
plot.show()
#==============================================================================