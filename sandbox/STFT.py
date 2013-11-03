from numpy import *

def hann(windowSize, hopSize):

    # Periodic window coefficients
    window = hanning(windowSize+1)
    window = window[0:windowSize]

    # Window weighting to reduce window energy over time
    weighting = sum(window*window)
    weighting = 1 / sqrt(weighting/hopSize)
    window *= weighting

    return window

def stft(signal, windowSize, hopSize):

    frameMarkers = range(0, len(signal)-windowSize, hopSize)
    frameCount = len(frameMarkers)

    # Preprocessed frame matrix [n,fft]
    frames = empty(
        (frameCount, windowSize/2+1),
        dtype=complex)

    # Periodic window coefficients
    window = hann(windowSize, hopSize)

    # Frame preprocessing
    for n in range(frameCount):
        m = frameMarkers[n]
        frame = signal[m:m+windowSize]
        frame = frame * window
        frame = fft.fftshift(frame)
        frame = fft.rfft(frame)
        frames[n] = frame

    return frames

def istft(frames, windowSize, hopSize):

    frameCount = len(frames)
    frameMarkers = [n*hopSize for n in range(frameCount)]

    # Output signal
    signal = zeros(frameMarkers[-1] + windowSize)

    # Periodic window coefficients
    window = hann(windowSize, hopSize)

    # Frame postprocessing
    for n in range(frameCount):
        frame = frames[n]
        frame = fft.irfft(frame)
        frame = fft.fftshift(frame)
        frame = frame * window
        m = frameMarkers[n]
        signal[m:m+windowSize] += frame

    return signal