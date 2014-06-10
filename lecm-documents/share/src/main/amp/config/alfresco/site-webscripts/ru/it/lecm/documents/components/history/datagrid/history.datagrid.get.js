<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    model.nodeRef = args["nodeRef"];
    model.dataSource = args["dataSource"];
    model.showSecondaryCheckBox = args["showSecondaryCheckBox"];
    model.bubblingLabel = args["bubblingLabel"];
}

main();