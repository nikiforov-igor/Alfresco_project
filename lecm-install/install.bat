@echo off

if ""%1""=="""" (
   echo alfresco path not set
) else (
    DEL %1\amps\*.amp
    COPY amps\alfresco\*.amp %1\amps
    DEL %1\amps_share\*.amp
    COPY amps\share\*.amp %1\amps_share
    %1\bin\apply_amps.bat
)
