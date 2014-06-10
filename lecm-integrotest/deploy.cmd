date /t
time /t

SET MODULE=lecm-integrotest

IF %TOMCAT%x==x SET TOMCAT=C:\Alfresco\tomcat

SET DEST_REPO=%TOMCAT%\webapps\alfresco
SET DEST_SHARE=%TOMCAT%\webapps\share

SET SRC_API=.\api\target\%MODULE%-api-0.5.0
SET SRC_REPO=.\repo\target\%MODULE%-repo-0.5.0
SET SRC_SHARE=.\share\target\%MODULE%-share-0.5.0

xcopy /Y /E %SRC_API%\config\*.*   %DEST_REPO%\WEB-INF\classes\
xcopy /Y %SRC_API%\lib\*.*   %DEST_REPO%\WEB-INF\lib\

rem xcopy /Y %SRC_REPO%\*.jar     %DEST_REPO%\WEB-INF\lib\
xcopy /Y /E %SRC_REPO%\config\*.*   %DEST_REPO%\WEB-INF\classes\
xcopy /Y %SRC_REPO%\lib\*.*   %DEST_REPO%\WEB-INF\lib\

xcopy /Y /E %SRC_SHARE%\config\*.*   %DEST_SHARE%\WEB-INF\classes\
xcopy /Y %SRC_SHARE%\lib\*.*   %DEST_SHARE%\WEB-INF\lib\
xcopy /Y %SRC_SHARE%\web\*.*   %DEST_SHARE%\

date /T
time /T

echo copied into %TOMCAT%/...