<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">


function main() {
    var nodeRef = remote.connect ("alfresco").get ("/lecm/groupActions/root");
    model.nodeRef = nodeRef;
}

main();