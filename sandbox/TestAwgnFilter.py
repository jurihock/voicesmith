from numpy import *
import matplotlib.pyplot as plot
import STFT as stft
import WAV as wav
from VAD import vad

x, sampleRate = wav.read("wav/tomsawyer.wav")

winSize = 1024*2      # samples
hopSize = winSize/4   # samples
hangoverTime = 200e-3 # seconds

vadFlags = vad(x, winSize, hopSize, sampleRate, hangoverTime)

plot.figure()
plot.plot(x, "b")
plot.plot(range(0, len(x)-winSize, hopSize), vadFlags/max(vadFlags)*max(x), "r")
plot.show()