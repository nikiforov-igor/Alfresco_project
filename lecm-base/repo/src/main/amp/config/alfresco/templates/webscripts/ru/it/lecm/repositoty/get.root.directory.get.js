function main()
{
    var
        resultNode = null,
        argsRootNode = args['rootNode'];

    if (logger.isLoggingEnabled()) {
        logger.log("argsRootNode = " + argsRootNode);
    }

    try {
        resultNode = resolveNode(argsRootNode);

        if (resultNode === null) {
            status.setCode(status.STATUS_NOT_FOUND, "Not a valid nodeRef: 'alfresco://user/temp'");
            return null;
        }
    } catch (e) {
        var msg = e.message;

        if (logger.isLoggingEnabled()) {
            logger.log(msg);
        }

        status.setCode(500, msg);

        return;
    }

    model.resultNode = resultNode.nodeRef.toString();
}

function resolveNode(reference) {
    var node = null;
    try {
        if (reference == "alfresco://company/home" || reference == "{companyhome}") {
            node = companyhome;
        } else if (reference == "alfresco://user/home") {
            node = userhome;
        } else if (reference == "alfresco://sites/home") {
            node = companyhome.childrenByXPath("st:sites")[0];
        } else if (reference == "alfresco://user/temp") {
            node = businessPlatform.getUserTemp();
        } else if (reference == "{organization}") {
            node = companyhome.childByNamePath("Организация");
        } else if (reference == "{lecmMyPrimaryUnit}") {
            node = orgstructure.getPrimaryOrgUnit(orgstructure.getCurrentEmployee());
        } else if (reference == "{lecmMyOrganization}") {
            node = orgstructure.getUnitByOrganization(orgstructure.getEmployeeOrganization(orgstructure.getCurrentEmployee()));
        }
    }
    catch (e) {
        return null;
    }
    return node;
}

main();