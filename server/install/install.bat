@echo off

if ""%1""=="""" (
   echo Alfresco path is not set. Use install.bat alfresco_home
) else (
    DEL %1\amps\*.amp
    COPY amps\alfresco\*.amp %1\amps
    DEL %1\amps_share\*.amp
    COPY amps\share\*.amp %1\amps_share
    %1\bin\apply_amps.bat nowait
)
