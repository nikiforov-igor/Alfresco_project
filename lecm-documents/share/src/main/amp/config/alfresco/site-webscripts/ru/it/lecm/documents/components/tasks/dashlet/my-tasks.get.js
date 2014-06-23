<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

model.nodeRef = args["nodeRef"];
model.hasStatemachine = hasStatemachine(args["nodeRef"]);
model.hasPermission = hasPermission(args["nodeRef"], PERM_WF_TASK_LIST);

model.isErrandsStarter = isStarter("lecm-errands:document");
