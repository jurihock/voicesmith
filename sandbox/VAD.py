from numpy import *

def vad(signal, windowSize, hopSize, smoothingGain, triggerThresholds):

    energyState  = [-100, 0] # [energy, velocity]
    triggerState = [0, -100] # [flag, energy]

    vadFlags = zeros(len(range(0, len(signal)-windowSize, hopSize)))
    vadFlagIndex = 0

    for n in range(0, len(signal)-windowSize, hopSize):

        frame = signal[n:n+windowSize]

        currentEnergy = rms2dbfs(rms(frame))
        energyState = smooth(energyState, currentEnergy, smoothingGain)
        currentDbfs = energyState[0]
        triggerState = trigger(triggerState, currentDbfs, triggerThresholds)

        vadFlags[vadFlagIndex] = triggerState[1]
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

        # TODO: optionally remove the signal offset
        # and apply window on frame overlapping

        if currFlag > lastFlag:
            result[n:n+windowSize] += frame*fadeIn
        elif currFlag < lastFlag:
            result[n:n+windowSize] += frame*fadeOut
        else:
            result[n:n+windowSize] += frame*currFlag

        lastFlag = currFlag
        i += 1

    return result

def trigger(state, value, thresholds):

    # Double threshold Schmitt trigger

    low = thresholds[0]
    high = thresholds[1]

    if value > state[1] and value > high:
        state[0] = 1
    elif value < state[1] and value < low:
        state[0] = 0

    state[1] = value

    return state

def smooth(state, value, gain):

    # Double state Luenberger observer to smooth
    # and if necessary to predict a single value

    prediction = state[0]
    error = value - prediction

    state[0] = prediction + gain[0]*error
    state[1] = state[1] + gain[1]*error

    prediction = sum(state)
    state[0] = prediction

    return state

def rms2dbfs(value):

    # Convert a RMS value to the full scale dB value relatively to 1

    value = min(max(value,1e-10),1) # crop too big/small values first

    return 10*log10(value) # not 20, because of squared amplitude!

def rms(frame):

    # Frame samples should be zero-mean (that is de facto the same as the variance)
    # to prevent dancing threshold values in the future step

    return sqrt(sum(power(frame-mean(frame),2))/len(frame))
