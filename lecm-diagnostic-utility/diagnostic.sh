#!/bin/sh
file="./utility-properties.cfg"
while read line
do
    var=$(echo $line | sed -e 's/.$//' | awk -F"=" '{a = $1 "=\"" $2 "\""; print a}')
    eval $var
done < $file

$alf_java_home/bin/java -cp ".;./lecm-diagnostic-utility-1.0.0.jar;./lib/*" -Dfile.encoding=UTF-8 ru.it.lecm.platform.DiagnosticUtility