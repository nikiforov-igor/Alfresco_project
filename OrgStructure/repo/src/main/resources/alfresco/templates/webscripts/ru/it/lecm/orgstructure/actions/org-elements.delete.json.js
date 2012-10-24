<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/action/action.lib.js">

/**
 * Delete multiple items action
 * @method DELETE
 */

/**
 * Entrypoint required by action.lib.js
 *
 * @method runAction
 * @param p_params {object} Object literal containing items array
 * @return {object|null} object representation of action results
 */
function runAction(p_params) {
    var results = [],
        items = p_params.items,
        item, result, nodeRef;

    // Must have array of items
    if (!items || items.length == 0) {
        status.setCode(status.STATUS_BAD_REQUEST, "No items supplied in JSON body.");
        return;
    }

    for (item in items) {
        nodeRef = items[item];
        result =
        {
            nodeRef:nodeRef,
            action:"deleteItem",
            success:false
        };

        try {
            var itemNode = search.findNode(nodeRef);
            if (itemNode != null) {
                // remove source assocs
                var sAssocs = itemNode.getSourceAssocs();
                for (key in sAssocs) {
                    var assocsList = sAssocs[key];
                    for(index in assocsList){
                        var target = assocsList[index];
                        target.removeAssociation(itemNode, key);
                    }
                }
                // mark as deleted
                itemNode.properties["lecm-dic:active"] = false;
                result.success = itemNode.save();
            }
        }
        catch (e) {
            result.success = false;
        }

        results.push(result);
    }

    return results;
}

/* Bootstrap action script */
main();
