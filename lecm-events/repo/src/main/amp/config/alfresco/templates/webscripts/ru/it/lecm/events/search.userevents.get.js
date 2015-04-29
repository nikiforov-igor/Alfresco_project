(function() {
	var searchTerm = args['searchTerm'];

	model.events = events.searchUserEvents(getFilterParams(searchTerm));
}());

function getFilterParams(filterData)
{
	var query = "";
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

		params += ampersand + replaceAll("-", "\\-", namespace[0]) +"\\:" + replaceAll("-", "\\-", namespace[1]) + ":"+ '(' + filter + ')' + or;
	}
	if (params !== "") {
		query += " AND " + "(" + params + " )";
	}
	return query;
}

function escapeString(str) {
	return str.replace(/"/g, '\\\"');
}

function trimString(str) {
	return str.replace(/^\s+|\s+$/g, '');
}

function replaceAll(find, replace, str) {
	return str.replace(new RegExp(find, 'g'), replace);
}