<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main(){
        AlfrescoUtil.param("nodeRef");

        model.mayView = hasPermission(model.nodeRef, '_lecmPerm_AttrList');
        model.mayAdd = hasPermission(model.nodeRef, '_lecmPerm_AttrEdit');
}

main();