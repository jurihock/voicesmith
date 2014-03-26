from numpy import *

def vad(signal, windowSize, hopSize, smoothingGain, triggerThresholds):

    initialOffset = 0
    initialDbfs = sum(triggerThresholds) / 2

    offsetState  = [initialOffset, 0, 0] # [mean, velocity, error]
    energyState  = [initialDbfs, 0, 0]   # [rms, velocity, error]
    triggerState = [0, initialDbfs]      # [flag, rms]

    frameMarkers = range(0, len(signal)-windowSize, hopSize)
    frameCount = len(frameMarkers)

    vadFlags = zeros((frameCount, 3)) # [trigger, offset, rms]
    i = 0

    for n in frameMarkers:

        frame = signal[n:n+windowSize]

        currentOffset = mean(frame)

        currentEnergy = rms2dbfs(rms(frame, currentOffset))
        energyState = smooth(energyState, currentEnergy, smoothingGain)
        currentEnergy = energyState[0]

        offsetState = smooth(offsetState, currentOffset, [0.025, 0])
        currentOffset = offsetState[0]

        triggerState = trigger(triggerState, currentEnergy, triggerThresholds)
        currentTrigger = triggerState[0]

        vadFlags[i,0] = currentTrigger
        vadFlags[i,1] = currentOffset
        vadFlags[i,2] = currentEnergy

        i += 1
        
    return vadFlags

def mute(signal, windowSize, hopSize, vadFlags):

    result = zeros(len(signal))

    frameMarkers = range(0, len(signal)-windowSize, hopSize)

    lastFlag = 0
    i = 0

    fadeIn  = linspace(0, 1, windowSize)
    fadeOut = linspace(1, 0, windowSize)

    for n in frameMarkers:

        frame = signal[n:n+windowSize]

        currFlag = vadFlags[i,0]
        currOffset = vadFlags[i,1]

        # TODO: apply window on frame overlapping

        if currFlag > lastFlag:
            result[n:n+windowSize] += multiply(frame - currOffset, fadeIn) + currOffset
        elif currFlag < lastFlag:
            result[n:n+windowSize] += multiply(frame - currOffset, fadeOut) + currOffset
        elif currFlag == 1:
            result[n:n+windowSize] += frame
        else:
            result[n:n+windowSize] = currOffset

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

    error = value - state[0]

    state[0] += state[1] + gain[0]*error
    state[1] += gain[1]*error
    state[2] = error

    return state

def rms2dbfs(value):

    # Convert a RMS value to the full scale dB value relatively to 1

    value = min(max(value,1e-10),1) # crop too big/small values first

    return 10*log10(value) # not 20, because of squared amplitude!

def rms(frame, offset):

    # Frame samples should be zero-mean (that is de facto the same as the variance)
    # to prevent dancing threshold values in the future step

    return sqrt(sum(power(frame-offset,2))/len(frame))
