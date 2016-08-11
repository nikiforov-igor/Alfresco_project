<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">
	function main() {
		var data = [],
			selectByOrg = !!review.isReviewersByOrganization()
		if (!!selectByOrg) {
			data = getPickerChildrenItems();
		} else {
			var filter = getFilterForAvailableElement(review.getPotentialReviewers());
			data = getPickerChildrenItems(filter);
		}

		model.parent = data.parent;
		model.rootNode = data.rootNode;
		model.results = data.results;
		model.additionalProperties = data.additionalProperties;
	};

main();