from numpy import *

class Oscillator():

    def __init__(self, sampleRate, waveFrequency, waveFunction=cos):

        self.sampleRate = sampleRate
        self.waveFrequency = waveFrequency
        self.waveFunction = waveFunction

        self.phaseDeviation = self.waveFrequency / self.sampleRate
        self.phaseDivisor = -self.phaseDeviation

    def getNextPhase(self):

        self.phaseDivisor += self.phaseDeviation

        while(self.phaseDivisor >= 1):
            self.phaseDivisor -= 1

        return 2 * pi * self.phaseDivisor

    def fillBuffer(self, buffer):

        for i in range(len(buffer)):
            buffer[i] = self.waveFunction(
                self.getNextPhase())

    def getBuffer(self, length):
        
        buffer = range(0, length)
        self.fillBuffer(buffer)
        return buffer
