from numpy import *
from scipy.io.wavfile import read, write
from subprocess import call, PIPE

""" Some helpful functions. """

def sound(data, samplerate, filepath="/tmp/sound.wav", player="play"):
    """ Stores given audio data in a wav file
        and finally plays it with the favorite audio software.
        The data array should be in the range [-1,1].
    """
    # Scale data to int16 range
    data_scaled = 32767 * data
    # Write the wav file
    write(filepath, samplerate, data_scaled.astype(int16))
    # Execute the wav player
    call(player + " " + filepath + " &", shell=True, stderr=PIPE)

def nextpow2(i):
    n = 2
    while n < i:
        n = n * 2
    return n

def hop_range(length, framesize, hopsize):
    i = 0
    while i < (length - framesize):
        yield i
        i += hopsize