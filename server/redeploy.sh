#!/bin/sh
#
ALF_HOME="$1"
CATALINA_HOME=$ALF_HOME/tomcat
echo "Redeploy LogicECM using: "
echo "ALF_HOME: $ALF_HOME"
echo "CATALINA_HOME: $CATALINA_HOME"

echo "Shutting down server"
.$ALF_HOME/alfresco.sh stop


echo "Starting server"
.$ALF_HOME/alfresco.sh start
