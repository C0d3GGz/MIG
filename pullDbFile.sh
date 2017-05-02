#!/bin/sh

# Credits: https://gist.github.com/timrijckaert/291cb8cdeee49e7529e0fd160e86c1c7
# Only works on rooted devices and emulators
packageName="${1:-de.familiep.mobileinformationgain}"
dbNameOnDevice="${2:-EventEntries.db}"
outputLocation="${3:-$HOME/Downloads/}"

adb start-server
adb shell mkdir /sdcard/tempdata

adb shell "su -c 'cp data/data/$packageName/databases/$dbNameOnDevice /sdcard/tempdata/$dbNameOnDevice'"
adb pull /sdcard/tempdata/$dbNameOnDevice $outputLocation
adb shell rm -r /sdcard/tempdata

exit 0
