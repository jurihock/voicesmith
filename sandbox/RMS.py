from numpy import *
import matplotlib.pyplot as plot
import WAV as wav

def rotate(array, n=1):
    n = n % len(array)
    return array[n:] + array[:n]

def rms(array):
    result = 0
    for i in range(len(array)):
        result += array[i] * array[i]
    return result # sqrt(result/len(array))

# Input data
x = wav.read("samples/test.wav")
x = x[0]

# Output data
y = zeros(len(x))

# Init RMS buffer
rmsBufferSize = 10;
rmsBuffer = zeros(rmsBufferSize);

# Fill output data
for i in range(len(x)):
    rmsBuffer[0] = x[i]
    y[i] = rms(rmsBuffer)
    bla = rotate(rmsBuffer)


# Plot I/O data
plot.figure()
plot.subplot(211)
plot.plot(x)
plot.subplot(212)
plot.plot(y)
plot.show()