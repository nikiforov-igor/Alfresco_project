<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/repository/forms/pickerresults.lib.js">

function getPickerItems() {
	var count = 0,
		items = [],
		results = [];

	// extract mandatory data from request body
	if (!json.has("items")) {
		status.setCode(status.STATUS_BAD_REQUEST, "items parameter is not present");
		return;
	}

	// convert the JSONArray object into a native JavaScript array
	var jsonItems = json.get("items"),
		itemValueType = "nodeRef",
		itemValueTypeHint = "",
		itemNameSubstituteString = "{cm:name}",
		substituteParent = "none",
		selectedItemsNameSubstituteString = "{cm:name}",
		numItems = jsonItems.length(),
		additionalProperties = json.has('additionalProperties') ? json.get('additionalProperties') : null,
		argsPathRoot = json.has('pathRoot') ? json.get('pathRoot') : null,
		argsPathNameSubstituteString = json.has('pathNameSubstituteString') ? json.get('pathNameSubstituteString') : null,
		argsUseObjectDescription = json.has('useObjectDescription') ? json.get('useObjectDescription') : false,
	item, result;

	if (additionalProperties != null && additionalProperties != "none") {
		additionalProperties = additionalProperties.split(',');
	}
	if (json.has("itemValueType")) {
		var jsonValueTypes = json.get("itemValueType").split(";");
		itemValueType = jsonValueTypes[0];
		itemValueTypeHint = (jsonValueTypes.length > 1) ? jsonValueTypes[1] : "";
	}
	if (json.has("itemNameSubstituteString")) {
		itemNameSubstituteString = json.get("itemNameSubstituteString");
	}
	if (json.has("substituteParent")) {
		substituteParent = "" + json.get("substituteParent");
	}
	if (json.has("selectedItemsNameSubstituteString")) {
		selectedItemsNameSubstituteString = json.get("selectedItemsNameSubstituteString");
	} else {
		selectedItemsNameSubstituteString = itemNameSubstituteString;
	}

	for (count = 0; count < numItems; count++) {
		item = jsonItems.get(count);
		if (item != "") {
			result = null;
			if (itemValueType == "nodeRef") {
				result = search.findNode(item);
				if (result != null && result.getStoreType() == "archive") {
					result = null;
				}
			}
			else if (itemValueType == "xpath") {
				result = search.xpathSearch(itemValueTypeHint.replace("%VALUE%", search.ISO9075Encode(item)))[0];
			}

			if (result != null) {
				// create a separate object if the node represents a user or group
				if (result.isSubType("cm:person")) {
					result = createPersonResult(result);
				}
				else if (result.isSubType("cm:authorityContainer")) {
					result = createGroupResult(result);
				}

				var path = "/";
				var simplePath = "/";

				if (argsPathRoot != null) {
					var rootNodes = search.xpathSearch(argsPathRoot);
					if (rootNodes.length > 0) {
						var pathRoot = rootNodes[0];
						var temp = result.parent;
						while (temp != null && temp.hasPermission("Read") && !temp.equals(pathRoot)) {
							var pathNodeName;
							if (argsPathNameSubstituteString != null) {
								if (argsPathNameSubstituteString.length > 0) {
									pathNodeName = substitude.formatNodeTitle(temp, argsPathNameSubstituteString);
								} else {
									pathNodeName = substitude.getObjectDescription(temp);
								}
							} else {
								pathNodeName = temp.name
							}

							path = "/" + pathNodeName + path;
							simplePath = "/_" + simplePath;
							temp = temp.parent;
						}
					}
				}

				var visibleName, selectedVisibleName;
				if (argsUseObjectDescription) {
					visibleName = substitude.getObjectDescription(substituteParent != "none" ? substituteParent : result.nodeRef.toString());
					selectedVisibleName = visibleName;
				} else {
					visibleName = substitude.formatNodeTitle(substituteParent != "none" ? substituteParent : result.nodeRef.toString(), ("" + itemNameSubstituteString));
					selectedVisibleName = substitude.formatNodeTitle(substituteParent != "none" ? substituteParent : result.nodeRef.toString(), ("" + selectedItemsNameSubstituteString));
				}

				results.push(
					{
						item: result,
						visibleName: visibleName,
						selectedVisibleName: selectedVisibleName,
						path: path,
						simplePath: simplePath
					});
			}
		}
	}

	if (logger.isLoggingEnabled())
		logger.log("#items = " + count + ", #results = " + results.length);

	return {
		results: results,
		additionalProperties: additionalProperties
	}
}
