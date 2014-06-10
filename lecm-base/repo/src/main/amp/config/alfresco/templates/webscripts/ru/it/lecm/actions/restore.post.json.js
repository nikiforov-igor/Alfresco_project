<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/action/action.lib.js">

function runAction(p_params) {
	var results = [],
		items = p_params.items,
		item, result, nodeRef;

	// Must have array of items
	if (!items || items.length == 0) {
		status.setCode(status.STATUS_BAD_REQUEST, "No items supplied in JSON body.");
		return [];
	}

	for (item in items) {
		nodeRef = items[item];
		result =
		{
			nodeRef: nodeRef,
			action: "restoreItem",
			success: false
		};

		try {
			var itemNode = search.findNode(nodeRef);
			if (itemNode != null) {
				itemNode.properties["lecm-dic:active"] = true;
				itemNode.save();
				result.success = true;
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
