<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

	function main() {
		var data = getPickerChildrenItems();
		var moveNode = null;
		var moveNodeArg = args['ignoreNodes'];
		if (moveNodeArg) {
			moveNode = search.findNode(args['ignoreNodes'])
		}
		for each (var res in data.results) {
			if (moveNode && res.item.properties["lecm-dic:valueContainsType"] && res.item.properties["lecm-dic:valueContainsType"].length > 0) {
				res.selectable = isItemSelectable(moveNode, res.item.properties["lecm-dic:valueContainsType"])
			}
		}

		model.parent = data.parent;
		model.rootNode = data.rootNode;
		model.results = data.results;
		model.additionalProperties = data.additionalProperties;
	}

main();