from numpy import *

# See also "Dynamical Energy-Based Speech/Silence Detector for Speech Enhancement Applications" by K. Sakhnov et al.

def vad(signal, windowSize, hopSize, sampleRate, noiseEnergy, hangoverTime):
    
    minEnergy = 0
    maxEnergy = 0

    minEnergyDelta = 1

    hangover = 0
    hangoverThreshold = ceil(hangoverTime * sampleRate / hopSize)

    vadFlags = zeros(len(range(0, len(signal)-windowSize, hopSize)))
    vadFlagIndex = 0

    for n in range(0, len(signal)-windowSize, hopSize):

        frame = signal[n:n+windowSize]
        currentEnergy = rms(frame)

        # TODO: Check this routine again!

        if (currentEnergy > maxEnergy):
            maxEnergy = currentEnergy
        
        if (currentEnergy < minEnergy):
            minEnergy = currentEnergy
            minEnergyDelta = 1

        if (minEnergy <= noiseEnergy):
            minEnergy = noiseEnergy
            minEnergyDelta = 1

        _lambda_ = (maxEnergy - minEnergy) / maxEnergy
        threshold = (1 - _lambda_) * maxEnergy + _lambda_ * minEnergy

        if(currentEnergy > threshold):
            hangover = 0
            vadFlags[vadFlagIndex] = 1
        elif (hangover == hangoverThreshold):
            vadFlags[vadFlagIndex] = 0
        else:
            hangover += 1
            vadFlags[vadFlagIndex] = 1

        minEnergyDelta *= 1.0001
        minEnergy *= minEnergyDelta

        vadFlagIndex += 1
        
    return vadFlags

def rms(frame):

    return sqrt(sum(power(frame,2))/len(frame))