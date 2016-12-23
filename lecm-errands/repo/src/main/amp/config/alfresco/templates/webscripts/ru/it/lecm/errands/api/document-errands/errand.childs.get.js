var errandNodeRef = args['nodeRef'];
var items = [];

var childErrands = errands.getChildErrands(errandNodeRef);
if (childErrands != null) {
	for (var i = 0; i < childErrands.length; i++) {
		var errand = childErrands[i];

		var executor = null;
		var executorAssoc = errand.assocs["lecm-errands:executor-assoc"];
		if (executorAssoc != null && executorAssoc.length == 1) {
			executor = executorAssoc[0];
		}

		if (executor != null) {
			items.push({
				"nodeRef": errand.nodeRef.toString(),
				"name": errand.properties["lecm-document:present-string"],
				"limitationDate": errand.properties["lecm-errands:limitation-date"],
				"executorNodeRef": executor.nodeRef.toString(),
				"executorName": executor.properties["lecm-orgstr:employee-short-name"],
				"autoClose": errand.properties["lecm-errands:auto-close"]
			});
		}
	}
}
model.items = items;
