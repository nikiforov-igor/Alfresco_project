<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/action/action.lib.js">

function isDependents()
{
    var isDependents = false;
    // Multiple input files in the JSON body?
    var items = getMultipleInputValues("nodeRefs");

    // Must have array of items
    if (!items || items.length == 0) {
        status.setCode(status.STATUS_BAD_REQUEST, "No items supplied in JSON body.");
        return;
    }

    for (item in items) {
        nodeRef = items[item];

        try {
            itemNode = search.findNode(nodeRef);
            if (itemNode != null) {
                var sAssocs;
                sAssocs = itemNode.getSourceAssocs();
                if (sAssocs.length > 0) {
                    isDependents = true;
                }
            }
        }
        catch (e) {
            isDependents = false;
        }

    }
    model.isDependents = isDependents;
}


isDependents();
