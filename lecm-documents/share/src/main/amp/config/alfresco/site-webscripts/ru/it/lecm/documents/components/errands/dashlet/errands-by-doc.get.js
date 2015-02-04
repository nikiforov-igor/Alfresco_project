<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

model.nodeRef = args["nodeRef"];
model.hasStatemachine = hasStatemachine(args["nodeRef"]);

model.isErrandsStarter = isStarter("lecm-errands:document");

model.hasPermission = hasPermission(model.nodeRef, PERM_ACTION_EXEC);
