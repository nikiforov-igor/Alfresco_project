@echo off

set JAVA_HOME=D:\Project\Alfresco\java
set M2_HOME=D:\Project\Tools\apache-maven-3.0.4
rem %M2_home%\bin\mvn -f ..\pom.xml package

if not exist .\amps\alfresco mkdir .\amps\alfresco
if not exist .\amps\share mkdir .\amps\share

del \amps\alfresco\*.*
del \amps\share\*.*

for /d %%F in (../*) do (
   if exist ..\%%F\repo\target\*.amp COPY ..\%%F\repo\target\*.amp .\amps\alfresco
   if exist ..\%%F\share\target\*.amp COPY ..\%%F\share\target\*.amp .\amps\share
)