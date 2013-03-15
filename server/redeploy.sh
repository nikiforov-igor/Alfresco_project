#!/bin/sh
#
ALF_HOME="$1"
CATALINA_HOME=$ALF_HOME/tomcat
echo "Redeploy LogicECM using: "
echo "ALF_HOME: $ALF_HOME"
echo "CATALINA_HOME: $CATALINA_HOME"

echo "Shutting down server"
$ALF_HOME/./alfresco.sh stop

echo "Clear SOLR indexes"
rm -rf $ALF_HOME/alf_data/solr/archive/SpacesStore
rm -rf $ALF_HOME/alf_data/solr/archive-SpacesStore/alfrescoModels
rm -rf $ALF_HOME/alf_data/solr/workspace/SpacesStore
rm -rf $ALF_HOME/alf_data/solr/workspace-SpacesStore/alfrescoModels

echo "Clear tomcat webapps"
rm -rf $CATALINA_HOME/webapps/alfresco
rm -rf $CATALINA_HOME/webapps/share

echo "Copy tomcat webapps"
cp -f server/alfresco/target/alfresco.war $CATALINA_HOME/webapps/alfresco
cp -f server/share/target/share.war $CATALINA_HOME/webapps/alfresco

echo "Starting server"
$ALF_HOME/./alfresco.sh start
