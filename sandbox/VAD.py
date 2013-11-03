from numpy import *

def vad(signal, windowSize, hopSize, sampleRate, hangoverTime):
    
    hangoverThreshold = ceil(hangoverTime * sampleRate / hopSize)

    delta = 1
    threshold = 0
    hangover = 0
    minEnergy = 0
    maxEnergy = 0
    
    vadFlags = zeros(len(range(0, len(signal)-windowSize, hopSize)))
    vadFlagIndex = 0

    for n in range(0, len(signal)-windowSize, hopSize):

        frame = signal[n:n+windowSize]
        currentEnergy = rms(frame)
        
        if (currentEnergy > maxEnergy):
            maxEnergy = currentEnergy
        
        if (currentEnergy < minEnergy):
            minEnergy = currentEnergy
            delta = 1

        # TODO: find out the optimal min energy level
        if (minEnergy < 0.01):
            minEnergy = 0.01 # power(min(frame),2)
            delta = 1

        delta *= 1.0001
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

        minEnergy *= delta
        
        vadFlagIndex += 1
        
    return vadFlags

def rms(frame):

    return sqrt(sum(power(frame,2))/len(frame))