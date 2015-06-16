<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

(function() {

	getFilterParams = function getFilterParams(filterData, parentXPath)
	{
		var query = " +PATH:\"" + parentXPath + "/*\"";
		var columns = [];
		if (filterData !== "") {
			columns = filterData.split('#');
		}

		var params = "",
			or = " OR",
			ampersand = " @";
		for (var i=0; i < columns.length; i++) {
			var namespace = columns[i].split(":");
			if (columns[i+1] == undefined ) {
				or = "";
				ampersand = " @";
			}

			var searchTerm = escapeString(trimString(namespace[2]));
			var searchArray = searchTerm.split(" ");
			var filter = "";
			for (var j = 0; j < searchArray.length; j++) {
				filter += '"*' + searchArray[j] + '*"';
				if (j < searchArray.length - 1) {
					filter += " OR ";
				}
			}

			params += ampersand + namespace[0]+"\\:" + namespace[1] + ":"+ '(' + filter + ')' + or;
		}
		if (params !== "") {
			query += " AND " + "(" + params + " )";
		}
		return query;
	}

	var data = getPickerChildrenItems();

	model.results = data.results;
	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.additionalProperties = data.additionalProperties;

})();