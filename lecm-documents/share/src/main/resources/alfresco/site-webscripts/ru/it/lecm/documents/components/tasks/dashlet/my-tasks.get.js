<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    model.nodeRef = args["nodeRef"];
    model.hasStatemachine = hasStatemachine(args["nodeRef"]);
}

main();