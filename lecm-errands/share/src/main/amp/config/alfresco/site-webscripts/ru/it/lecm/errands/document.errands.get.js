<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

(function() {
    AlfrescoUtil.param("nodeRef");
    if (model.nodeRef) {
        model.hasLinksViewPerm = hasPermission(model.nodeRef, PERM_LINKS_VIEW);
    }
})();