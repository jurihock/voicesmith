from numpy import *
import matplotlib.pyplot as plot
import QuickAndDirty as qad

Fs = 8000
t = arange(0, 1, 1.0/Fs)
f = 440
x = sin(2*pi*f*t)

length = x.size
framesize = 1024
hopsize = framesize/4
window = hanning(framesize)/2

y = zeros(length)

for i in qad.hop_range(length, framesize, hopsize):
    frame = x[i:i+framesize]
    frame = frame * window
    y[i:i+framesize] = y[i:i+framesize] + frame

#qad.sound(x, Fs, filepath="/tmp/x.wav");
qad.sound(y, Fs, filepath="/tmp/y.wav");

exit(0)

plot.figure()
tn = 4 * round(Fs/f) + 1;
plot.subplot(211)
plot.plot(x[0:tn])
plot.subplot(212)
plot.plot(y[0:tn])
plot.show()