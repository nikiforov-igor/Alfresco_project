    <import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
    <import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">
    <import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
    <import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

    function main() {
        AlfrescoUtil.param('nodeRef');
        AlfrescoUtil.param('site', null);
        AlfrescoUtil.param('container', 'documentLibrary');
        AlfrescoUtil.param('baseDocAssocName', null);

        var baseDocRef = getBaseDocNodeRef(model.nodeRef, model.baseDocAssocName);
        model.hasPermission = false;
        if (baseDocRef) {
            model.baseDocRef = baseDocRef;
            if (isFinalStatus(baseDocRef)) {
                var documentDetails = DocumentUtils.getNodeDetails(baseDocRef, model.site, {
                    actions: true
                });
                model.documentDetails = documentDetails;
                if (documentDetails) {
                    model.documentDetailsJSON = jsonUtils.toJSONString(documentDetails);
                    doclibCommon();
                }
            }
            model.hasPermission = hasPermissions(baseDocRef);
        }
        model.isAdmin = user.isAdmin;
    }

function isFinalStatus(nodeRef, defaultValue) {
    var url = '/lecm/statemachine/isFinal?nodeRef=' + nodeRef;
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        if (defaultValue !== undefined) {
            return defaultValue;
        }
        return false;
    }
    return eval('(' + result + ')').isFinal;
}

function getBaseDocNodeRef(nodeRef, baseDocAssocName) {
    var url = '/lecm/document/api/base?nodeRef=' + nodeRef + "&baseDocAssocName=" +baseDocAssocName;
    var result = remote.connect("alfresco").get(url);
    if (result.status == 200) {
        return eval('(' + result + ')').baseDocNodeRef;
    }
    else return null;
}

function hasPermissions(nodeRef) {
    var actionExecPerm = hasPermission(nodeRef, PERM_ACTION_EXEC);
    var draftPerm  = hasOnlyInDraftPermission(nodeRef, "LECM_BASIC_PG_Initiator");
    var readPerm = hasPermission(nodeRef, "Read");
    return (actionExecPerm || draftPerm) && readPerm;
}
main();