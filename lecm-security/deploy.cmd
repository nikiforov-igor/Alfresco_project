SET MODULE=lecm-security

SET TOMCAT=C:\Alfresco\tomcat

SET DEST_REPO=%TOMCAT%\webapps\alfresco
SET DEST_SHARE=%TOMCAT%\webapps\share

rem SET SRC_REPO=.\repo\target\%MODULE%-repo-0.1.0
SET SRC_REPO=.\repo\target

SET SRC_SHARE=.\share\target\%MODULE%-share-0.1.0

rem xcopy /Y /E %SRC_REPO%\config\*.*   %DEST_REPO%\WEB-INF\classes\
rem xcopy /Y %SRC_REPO%\lib\*.*   %DEST_REPO%\WEB-INF\lib\
xcopy /Y %SRC_REPO%\*.jar     %DEST_REPO%\WEB-INF\lib\

xcopy /Y /E %SRC_SHARE%\config\*.*   %DEST_SHARE%\WEB-INF\classes\
xcopy /Y %SRC_SHARE%\lib\*.*   %DEST_SHARE%\WEB-INF\lib\

date /T
time /T