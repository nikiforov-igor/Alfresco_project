<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

AlfrescoUtil.param("nodeRef");
model.hasPermission = hasPermission(model.nodeRef, PERM_WF_TASK_LIST);