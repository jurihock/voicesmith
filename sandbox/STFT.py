from numpy import *

def hann(windowSize, hopSize):

    # Periodic window coefficients
    window = hanning(windowSize+1)
    window = window[0:windowSize]

    # Window weighting
#    weighting = sum(window*window)
#    weighting = 1 / sqrt(weighting/hopSize)
#    window *= weighting

    return window

def stft(signal, windowSize, hopSize):

    # Preprocessed frame matrix [n,fft]
    frames = empty(
        (len(signal)-windowSize, windowSize/2+1),
        dtype=complex)

    # Periodic window coefficients
    window = hann(windowSize, hopSize)

    # Frame preprocessing
    for n in range(0, len(signal)-windowSize):
        frame = signal[n:n+windowSize]
        frame = frame * window
        frame = fft.fftshift(frame)
        frame = fft.rfft(frame)
        frames[n] = frame

    return frames

def istft(frames, windowSize, hopSize):

    # Output signal
    signal = zeros(len(frames)+windowSize)

    # Periodic window coefficients
    window = hann(windowSize, hopSize)

    # Frame postprocessing
    for n in range(0, len(frames)):
        frame = frames[n]
        frame = fft.irfft(frame) / len(frame)
        frame = fft.fftshift(frame)
        frame = frame * window
        signal[n:n+windowSize] += frame

    return signal