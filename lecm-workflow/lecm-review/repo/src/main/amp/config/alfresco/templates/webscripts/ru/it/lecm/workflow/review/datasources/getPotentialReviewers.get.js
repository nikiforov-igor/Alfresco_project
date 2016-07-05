<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">
	function main() {
		var data = [];

		if (review.isReviewersByOrganization() == false) {
			var filter = getFilterForAvailableElement(review.getPotentialReviewers());
			data = getPickerChildrenItems(filter);
		} else {
			data = getPickerChildrenItems();
		}

		model.parent = data.parent;
		model.rootNode = data.rootNode;
		model.results = data.results;
		model.additionalProperties = data.additionalProperties;
	};

main();