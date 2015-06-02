<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var approval = getApproval(model.nodeRef);
    if (approval != null) {
        model.approval = jsonUtils.toJSONString(approval);
    }
}

function getApproval(nodeRef) {
    var url = '/lecm/approval/getApprovalInfoForDocument?nodeRef=' + nodeRef + "";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return null;
    }
    return eval('(' + result + ')');
}

main();
