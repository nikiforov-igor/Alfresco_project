<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    model.nodeRef = args["nodeRef"];
    model.hasPermission = hasPermission(model.nodeRef, '_lecmPerm_AttrList');
}

main();