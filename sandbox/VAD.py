from numpy import *

def vad(signal, windowSize, hopSize, smoothGain, triggerThresholds):

    energyState = [0, 0]
    triggerState = [0, 0]

    vadFlags = zeros(len(range(0, len(signal)-windowSize, hopSize)))
    vadFlagIndex = 0

    for n in range(0, len(signal)-windowSize, hopSize):

        frame = signal[n:n+windowSize]

        currentEnergy = rms2dbfs(rms(frame))
        energyState = smooth(energyState, currentEnergy, smoothGain)
        currentDbfs = energyState[0]
        triggerState = trigger(triggerState, currentDbfs, triggerThresholds)

        vadFlags[vadFlagIndex] = triggerState[0]
        vadFlagIndex += 1
        
    return vadFlags

def mute(signal, windowSize, hopSize, vadFlags):

    result = zeros(len(signal))

    lastFlag = 0
    i = 0

    fadeIn  = linspace(0, 1, windowSize)
    fadeOut = linspace(1, 0, windowSize)

    for n in range(0, len(signal)-windowSize, hopSize):

        frame = signal[n:n+windowSize]

        currFlag = vadFlags[i]

        if currFlag > lastFlag:
            result[n:n+windowSize] = frame*fadeIn
        elif currFlag < lastFlag:
            result[n:n+windowSize] = frame*fadeOut
        else:
            result[n:n+windowSize] = frame*currFlag

        lastFlag = currFlag
        i += 1

    return result

def trigger(state, value, thresholds):

    low = thresholds[0]
    high = thresholds[1]

    if value > state[1] and value > high:
        state[0] = 1
    elif value < state[1] and value < low:
        state[0] = 0

    state[1] = value

    return state

def rms2dbfs(value):

    return 10*log10(value)

def smooth(state, value, gain):

    prediction = sum(state)
    error = value - prediction

    state[0] = prediction + gain[0]*error
    state[1] = state[1] + gain[1]*error

    return state

def rms(frame):

    # TODO: check the meaning of removed mean
    return sqrt(sum(power(frame-mean(frame),2))/len(frame))