<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("formId");
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
    if (nodeDetails) {
        model.node = nodeDetails.item.node;
    }

    var atts = getAttachments(model.nodeRef, "Поручение");
    if (atts != null) {
        model.attachments = atts;
    }
    atts = getAttachments(model.nodeRef, "Исполнение");
    if (atts != null) {
        model.attachmentsExec = atts;
    }
    atts = getAttachments(model.nodeRef, "Контроль");
    if (atts != null) {
        model.attachmentsControl = atts;
    }

    var coexecs = getCoexecutors(model.nodeRef);
    if (coexecs != null) {
        model.coexecs = coexecs;
    }

    var additionalDoc = getAdditonalDoc(model.nodeRef);
    if (additionalDoc != null) {
        model.additionalDoc = additionalDoc;
    }

    model.limitDate = new Date(nodeDetails.item.node.properties["lecm-errands:limitation-date"]["value"]);
}

function getAttachments(nodeRef, categoryName) {
    var url = '/lecm/document/attachments/api/getAttachmentsByCategory?documentNodeRef=' + nodeRef + '&category=' + encodeURIComponent(categoryName);
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return null;
    }
    return eval('(' + result + ')');
}
function getCoexecutors(nodeRef) {
    //todo: change members to coexecutors
    var url = '/lecm/document/api/getMembers?nodeRef=' + nodeRef + "&skipCount=0&loadCount=10";
    var result = remote.connect("alfresco").get(url);

    if (result.status != 200) {
        return null;
    }
    return eval('(' + result + ')');
}
function getAdditonalDoc(nodeRef) {
    var url = '/lecm/errands/api/getAdditionalDoc?nodeRef=' + nodeRef;
    var result = remote.connect("alfresco").get(url);

    if (result.status != 200) {
        return null;
    }
    return eval('(' + result + ')');
}

main();