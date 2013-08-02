<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("formId");
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
    if (nodeDetails) {
        model.node = nodeDetails.item.node;
    }

    var atts = getAttachments(model.nodeRef);
    if (atts != null) {
        model.attachments = atts;
    }

    var coexecs = getCoexecutors(model.nodeRef);
    if (coexecs != null) {
        model.coexecs = coexecs;
    }

    model.limitDate = new Date(nodeDetails.item.node.properties["lecm-errands:limitation-date"]["value"]);
}

function getAttachments(nodeRef, defaultValue) {
    var url = '/lecm/document/attachments/api/get?documentNodeRef=' + nodeRef + "&count=10";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        if (defaultValue !== undefined) {
            return defaultValue;
        }
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

main();