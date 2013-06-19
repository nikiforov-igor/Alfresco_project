@echo off

set "CURRENT_DIR=%~dp0%"
set "EXECUTABLE=%CURRENT_DIR%\tomcat6.exe"

echo %EXECUTABLE%
%EXECUTABLE% //US//alfrescoTomcat ++JvmOptions "-Dfile.encoding=UTF-8;"
pause
