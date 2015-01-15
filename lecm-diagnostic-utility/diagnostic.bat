@echo off

for /f "tokens=1,2 delims=^=" %%a in (.\utility-properties.cfg) do (
 set "%%a=%%b"
)

%alf_java_home%\bin\java -cp ".;.\lecm-diagnostic-utility.jar;.\lib\*" -Dfile.encoding=UTF-8 ru.it.lecm.platform.DiagnosticUtility
