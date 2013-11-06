from numpy import *
import matplotlib.pyplot as plot

# Visualize relation between VAD threshold and sample RMS signal values

# Make sample Emax and Emin values

x = arange(0, 2*math.pi, 0.0001) # x-values
y = 0.05 # y-offset

e_max = sin(x)*(1-y)
e_max[len(x)/2:len(x)] = 1-y
e_max += y

e_min = -sin(x)*(1-y)
e_min[0:len(x)/2] = 0
e_min += y

# Compute threshold

_lambda_ = (e_max-e_min)/e_max
thresh = (1-_lambda_)*e_max + _lambda_*e_min

# Show results

plot.figure()
plot.plot(x, e_max, "b")
plot.plot(x, e_min, "b--")
plot.plot(x, _lambda_, "g")
plot.plot(x, thresh, "r", linewidth=3)
plot.legend(["Emax", "Emin", "Lambda", "Threshold"])
plot.show()