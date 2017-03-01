    <import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
    <import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">
    <import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

    function main() {
        AlfrescoUtil.param('nodeRef');
        AlfrescoUtil.param('site', null);
        AlfrescoUtil.param('container', 'documentLibrary');
        AlfrescoUtil.param('baseDocAssocName', null);

        var document = DocumentUtils.getNodeDetails(model.nodeRef).item.node;
        var baseDocRef = null;

        if (document != null && model.baseDocAssocName != null) {
            baseDocRef = document.properties[model.baseDocAssocName + "-ref"];
        }

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
        }

        //var hasPerm = hasPermission(model.nodeRef, PERM_ACTION_EXEC);
        model.hasPermission = true; // hasPerm || hasOnlyInDraftPermission(model.nodeRef, "LECM_BASIC_PG_Initiator");
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

main();