<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    model.nodeRef = args["nodeRef"];
    model.hasPermission = hasPermission(model.nodeRef, PERM_ATTR_LIST);
    model.mayAdd = hasPermission(model.nodeRef, PERM_ATTR_EDIT);
    model.hasStatemachine = hasStatemachine(model.nodeRef);
}

main();