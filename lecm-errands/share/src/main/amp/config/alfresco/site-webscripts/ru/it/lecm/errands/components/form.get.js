<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("formId");
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
    if (nodeDetails) {
        model.node = nodeDetails.item.node;
    }

    var uri = '';

    uri = addParamToUrl('/lecm/document/attachments/api/getAttachmentsByCategory', 'documentNodeRef', model.nodeRef);
    uri = addParamToUrl(uri, 'category', encodeURIComponent("Поручение"));
    model.attachments = doGetCall(uri);

    uri = addParamToUrl('/lecm/document/attachments/api/getAttachmentsByCategory', 'documentNodeRef', model.nodeRef);
    uri = addParamToUrl(uri, 'category', encodeURIComponent("Исполнение"));
    model.attachmentsExec = doGetCall(uri);

    uri = addParamToUrl('/lecm/document/attachments/api/getAttachmentsByCategory', 'documentNodeRef', model.nodeRef);
    uri = addParamToUrl(uri, 'category', encodeURIComponent("Контроль"));
    model.attachmentsControl = doGetCall(uri);

    uri = addParamToUrl('/lecm/errands/api/getCoexecutors', 'nodeRef', model.nodeRef);
    model.coexecs = doGetCall(uri);

	uri = addParamToUrl('/lecm/errands/api/getChildErrands', 'nodeRef', model.nodeRef);
	model.childErrands = doGetCall(uri);

    uri = addParamToUrl('/lecm/errands/api/getAdditionalDoc', 'nodeRef', model.nodeRef);
    model.additionalDoc = doGetCall(uri);

    uri = addParamToUrl('/lecm/errands/api/getLinks', 'nodeRef', model.nodeRef);
    uri = addParamToUrl(uri, 'assocType', 'lecm-errands:links-assoc');
    model.links = doGetCall(uri);

    uri = addParamToUrl('/lecm/errands/api/getLinks', 'nodeRef', model.nodeRef);
    uri = addParamToUrl(uri, 'assocType', 'lecm-errands:execution-links-assoc');
    model.executeLinks = doGetCall(uri);

    model.limitDate = nodeDetails.item.node.properties["lecm-errands:limitation-date"];

	uri = addParamToUrl('/lecm/errands/api/getCurrentEmployeeRoles', 'errandNodeRef', model.nodeRef);
	model.roles = doGetCall(uri);

	model.hasViewContentListPerm = hasPermission(model.nodeRef, PERM_CONTENT_LIST);
	model.hasViewAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_VIEW);
	model.hasAddAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_ADD);
	model.hasActionExecPerm = hasPermission(model.nodeRef, PERM_ACTION_EXEC);

	model.hasAttrEditPerm = hasPermission(model.nodeRef, PERM_ATTR_EDIT);
	//TODO Что то много запросов
	uri = addParamToUrl('/lecm/statemachine/api/field/editable', 'nodeRef', model.nodeRef);
	uri = addParamToUrl(uri, 'field', 'lecm-errands:child-errands');
	model.isEditableChildErrands = doGetCall(uri);

	uri = addParamToUrl('/lecm/statemachine/api/field/editable', 'nodeRef', model.nodeRef);
	uri = addParamToUrl(uri, 'field', 'lecm-errands:links-assoc');
	model.isEditableLinks = doGetCall(uri);

	uri = addParamToUrl('/lecm/statemachine/api/field/editable', 'nodeRef', model.nodeRef);
	uri = addParamToUrl(uri, 'field', 'lecm-errands:execution-report');
	model.isEditableExecutionReport = doGetCall(uri);

	uri = addParamToUrl('/lecm/statemachine/api/field/editable', 'nodeRef', model.nodeRef);
	uri = addParamToUrl(uri, 'field', 'lecm-errands:execution-links-assoc');
	model.isEditableExecutionLinks = doGetCall(uri);

	model.isErrandsStarter = isStarter("lecm-errands:document");

    model.hasStatemachine = hasStatemachine(model.nodeRef);
}

main();
