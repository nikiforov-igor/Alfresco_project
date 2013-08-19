date /t
time /t

SET MODULE=lecm-security

IF %TOMCAT%x==x SET TOMCAT=C:\Alfresco\tomcat

IF %LECM_BUILDVER%x==x SET LECM_BUILDVER=0.5.1
echo coping module %MODULE% version %LECM_BUILDVER%

SET DEST_REPO=%TOMCAT%\webapps\alfresco
SET DEST_SHARE=%TOMCAT%\webapps\share

SET SRC_API=.\api\target\%MODULE%-api-%LECM_BUILDVER%
SET SRC_REPO=.\repo\target\%MODULE%-repo-%LECM_BUILDVER%
SET SRC_SHARE=.\share\target\%MODULE%-share-%LECM_BUILDVER%

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