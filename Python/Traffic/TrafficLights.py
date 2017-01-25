import csv
import urllib.request

url = "http://pghgis.pittsburghpa.opendata.arcgis.com/datasets/d7113fcd9cea485098f94530be1095a7_0.csv"
response = urllib.request.urlopen(url, 'rt')
cr = csv.reader(response)

for row in cr:
    print(row)
