<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">

function main() {
	var attachments = [];
	var fields;
	var nameSubstituteStrings;
	var sort = null;

	if (typeof json !== "undefined" && json.has("params")) {
		var pars = json.get("params");
		var categoryRef = (pars.get("parent").length() > 0) ? pars.get("parent") : null;

		attachments = documentAttachments.getAttachmentsByCategory(categoryRef);
		fields = (pars.get("fields").length() > 0) ? pars.get("fields") : null;
		nameSubstituteStrings = (pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null;
		sort = (pars.get("sort").length() > 0) ? pars.get("sort") : null;
	}

	var sortField = {column: "cm:name", ascending: true};

	if (sort != null && ("" + sort).length > 0) {
		var asc = true;
		var separator = sort.indexOf("|");
		if (separator != -1) {
			asc = (sort.substring(separator + 1) == "true");
			sort = sort.substring(0, separator);
		}
		sortField.column = sort;
		sortField.ascending = asc;
	}

	attachments.sort(function (a, b) {
		var value1 = a.properties[sortField.column];
		var value2 = b.properties[sortField.column];

		if (value1 < value2) {
			return -1;
		} else if (value1 > value2) {
			return 1;
		} else {
			return 0;
		}
	});
	if (!sortField.ascending) {
		attachments.reverse();
	}

	var result = processResults(attachments, fields, nameSubstituteStrings, 0, attachments.length); // call method from search.lib.js
	if (result != null && result.items != null) {
		for (var i = 0; i < result.items.length; i++) {
			result.items[i].isInnerAttachment = documentAttachments.isInnerAttachment(result.items[i].node);
		}
	}

	model.data = result;
}

main();
