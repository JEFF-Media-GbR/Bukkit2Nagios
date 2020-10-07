#!/usr/bin/env python
import subprocess
import sys

HOST = "localhost"
PORT = 9991

LINESEPARATOR = ""
FIELDSEPARATOR = ""

COMMAND = ["nc", "$HOST $PORT"]

MAGIC="Bukkit2Nagios"

def extractPerformanceData(key,value):
  if key == "tps 1m":
    print("Processing TPS 1 min")


try:
  result = subprocess.check_output("/usr/bin/nc localhost 9991 -w 3", shell=True)
except subprocess.CalledProcessError as e:
  print("CRITICAL - Could not connect")
  quit()

splitResult = result.split("\n",2)

if(len(splitResult) != 3):
  print ("CRITICAL - Invalid number of output lines ("+len(splitResult)+")")
  quit()

if MAGIC not in splitResult[0]:
  print ("CRITICAL - No valid input")
  quit()
else:
  print ("Remote version: "+splitResult[0])

FIELDSEPARATOR=splitResult[1]
LINESEPARATOR=";"+FIELDSEPARATOR+";"+FIELDSEPARATOR+";"
PERFORMANCE=list()

for line in splitResult[2].split(LINESEPARATOR):
  if FIELDSEPARATOR in line:
  	entry = line.split(FIELDSEPARATOR,2)
  	key = entry[0]
  	name = entry[1]
  	value = entry[2]
  	print (name+": "+value)
  	extractPerformanceData(key,value)
  elif not line:
    print ("Invalid data: "+line)

