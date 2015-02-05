<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    AlfrescoUtil.param("errandsUrl");
    AlfrescoUtil.param("createButton");
    AlfrescoUtil.param("label");
    AlfrescoUtil.param("isAnchor");

    model.hasStatemachine = hasStatemachine(model.nodeRef);

	model.isErrandsStarter = isStarter("lecm-errands:document");
}

main();