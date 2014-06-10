#!/bin/sh
#
ALF_HOME="$1"
CATALINA_HOME=$ALF_HOME/tomcat
echo "Redeploy LogicECM using: "
echo "ALF_HOME: $ALF_HOME"
echo "CATALINA_HOME: $CATALINA_HOME"

echo "Shutting down server"
$ALF_HOME/./alfresco.sh stop

echo "Clear tomcat webapps"
rm -rf $CATALINA_HOME/webapps/alfresco
rm -rf $CATALINA_HOME/webapps/share

echo "Copy tomcat webapps"
cp -f server/alfresco/target/alfresco.war $CATALINA_HOME/webapps
cp -f server/share/target/share.war $CATALINA_HOME/webapps

echo "Starting server"
$ALF_HOME/./alfresco.sh start
