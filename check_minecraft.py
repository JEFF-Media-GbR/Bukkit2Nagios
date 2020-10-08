#!/usr/bin/env python
import subprocess
import sys
import json
from collections import OrderedDict
import getopt

HOST = "localhost"
PORT = "9991"

LINESEPARATOR = ""
FIELDSEPARATOR = ""

COMMAND = "curl -s telnet://"+HOST+":"+PORT

PLUGINS_DISABLED_WARN = 1
PLUGINS_DISABLED_CRIT = 2


outputJson = False
outputTable = False
status = 0

try:
    opts, args = getopt.getopt(sys.argv[1:],"jt",["--json","--table"])
except getopt.GetoptError:
    print 'check_minecraft.py -j'
    sys.exit(2)
for opt, arg in opts:
    if opt == '-j':
        outputJson = True
    if opt == '-t':
        outputTable = True

def extractPerformanceData(key,value):
  if key == "tps 1m":
    print("Processing TPS 1 min")

def setStatus(newStatus):
    if newStatus > status:
        status = newStatus

def checkStatus(dict):
    for key in dict:
        if key == "Plugins disabled":
            if int(dict[key]) >= PLUGINS_DISABLED_WARN:
                setStatus(1)
            if int(dict[key]) >= PLUGINS_DISABLED_CRIT:
                setStatus(2)

def getStatus(status):
    if status == 0:
        return "OK"
    if status == 1:
        return "WARN"
    if status == 2:
        return "CRIT"

try:
  result = subprocess.check_output(COMMAND, shell=True)
except subprocess.CalledProcessError as e:
  print("CRITICAL - Could not connect")
  quit()


if outputJson:
    print(result)
    sys.exit(0)


PERFORMANCE=list()



dict = json.loads(result, object_pairs_hook=OrderedDict)

if outputTable:
    for key in dict:
        print "{:<20} {}".format(key, dict[key])
    sys.exit(0)

output = ""

for key in dict:
    output += key + ": " + dict[key] + ", "

print(str(getStatus(status)) + " - " + output)

