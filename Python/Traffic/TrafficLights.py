# This data is from https://data.wprdc.org/dataset/pittsburgh-traffic-signals/resource/771023db-a691-4a2e-9f98-cd62621365d4
# As practice reading CSV files, I am parsing through the list and calculating various statistics

import csv
import numpy as np

csvfile = open("Pittsburgh_Traffic_Signals.csv")
cr = csv.reader(csvfile)

long_array = []
lat_array = []

for row in cr:
    try:
        x = float(row[0])
        y = float(row[1])
    except ValueError:
        continue
    long_array.append(y)
    lat_array.append(x)

np_long = np.array(long_array)
np_lat = np.array(lat_array)

print("Here are various statistics relating to Pittsburgh traffic lights")

#number of traffic lights
num_lights = len(long_array)
print("Number of lights:", num_lights)

#avg latitude
avg_lat = np.average(np_lat)
print("Average latitude:", avg_lat)


#avg longitude
avg_long = np.average(np_long)
print("Average longitude:", avg_long)

#Easternmost location
print("Easternmost location:", np.amax(np_lat))

#Westernmost location
print("Westernmost location:", np.amin(np_lat))

#Northernmost location
print("Northernmost location:", np.amax(np_long))

#Southermost location
print("Southernmost location:", np.amin(np_long))
