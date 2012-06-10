from numpy import *

import scipy.io.wavfile as wav
import subprocess as proc

def read(filepath):

    """
    Reads data from a WAV file

    Parameters
    ----------
    filepath : file
        The file path to read

    Returns
    -------
    data : ndarray
        Float array in range [-1,1]
    rate : int
        Sample rate of WAV file
    """
    
    samplerate, data = wav.read(filepath)
    
    # Scale data to [-1,1] range
    data_scaled = data / 32767.0
    
    return data_scaled, samplerate

def write(data, samplerate, filepath):

    """
    Writes data as a WAV file

    Parameters
    ----------
    data : ndarray
        A float array in range [-1,1]
    samplerate : int
        The sample rate in Hz
    filepath : file
        The file path to write (will be over-written)
    """

    # Scale data to int16 range
    data_scaled = data * 32767.0

    # Write the WAV file
    wav.write(filepath, samplerate, data_scaled.astype(int16))

def sound(data, samplerate, filepath="/tmp/sound.wav", player="play"):

    """
    Plays data with the favorite audio software
    """

    # Write data as a WAV file
    write(data, samplerate, filepath)

    # Execute the external WAV player
    proc.call(player + " " + filepath + " &", shell=True, stderr=proc.PIPE)