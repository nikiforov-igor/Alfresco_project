(function() {
	var searchTerm = args['searchTerm'];
	var extSearchData = args["extSearchData"];
    var timeZoneOffset = null;
    if (args['timeZoneOffset']) {
        timeZoneOffset = parseInt(args['timeZoneOffset']);
    }

	var query = "";
	if (searchTerm.length > 0) {
		query += "(" + getFilterParams(searchTerm) + ")";
	}
	if (extSearchData.length > 0) {
		var extSearchQuery = getExtSearchQuery(jsonUtils.toObject(extSearchData));
		if (extSearchQuery.length > 0) {
			if (query.length > 0) {
				query += " AND ";
			}
			query += "(" + extSearchQuery + ")";
		}
	}

	model.events = events.searchUserEvents(query, timeZoneOffset);
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
		query += params;
	}
	return query;
}

function getExtSearchQuery(formJson) {
	var formQuery = "";
	var itemType = "lecm-events:document"; // заменяем пришедший тип на тип из формы

	// extract form data and generate search query
	var first = true;
	for (var p in formJson) {
		// retrieve value and check there is someting to search for
		// currently all values are returned as strings
		var propValue = formJson[p];
		if (propValue.length !== 0) {
			if (p.indexOf("prop_") === 0) {
				// found a property - is it namespace_propertyname or pseudo property format?
				var propName = p.substr(5);
				if (propName.indexOf("_") !== -1) {
					// property name - convert to DD property name format
					propName = propName.replace("_", ":");

					// special case for range packed properties
					if (propName.match("-range$") == "-range") {
						// currently support text based ranges (usually numbers) or date ranges
						// range value is packed with a | character separator

						// if neither value is specified then there is no need to add the term
						if (propValue.length > 1) {
							var from, to, sepindex = propValue.indexOf("|");
							if (propName.match("-date-range$") == "-date-range") {
								// date range found
								propName = propName.substr(0, propName.length - "-date-range".length)

								// work out if "from" and/or "to" are specified - use MIN and MAX otherwise;
								// we only want the "YYYY-MM-DD" part of the ISO date value - so crop the strings
								from = (sepindex === 0 ? "MIN" : propValue.substr(0, 10));
								to = (sepindex === propValue.length - 1 ? "MAX" : propValue.substr(sepindex + 1, 10));
							}
							else {
								// simple range found
								propName = propName.substr(0, propName.length - "-number-range".length);

								// work out if "min" and/or "max" are specified - use MIN and MAX otherwise
								from = (sepindex === 0 ? "MIN" : propValue.substr(0, sepindex));
								to = (sepindex === propValue.length - 1 ? "MAX" : propValue.substr(sepindex + 1));
							}
							formQuery += (first ? '' : ' AND ') + escapeQName(propName) + ':"' + from + '".."' + to + '"';
							first = false;
						}
					} else {
						if (propName.match("-strong-constant$") == "-strong-constant") {
							propName = propName.substr(0, propName.length - "-strong-constant".length);
							propValue = '"' + propValue + '"';
						} else {
							if (propValue != null && propValue != "") {
								var searchTermsArray = propValue.split(" ");
								var searchTerm = "";
								for (var k = 0; k < searchTermsArray.length; k++) {
									var newSearchTerm = searchTermsArray[k];
									if (newSearchTerm != null && newSearchTerm != "") {
										if (k > 0) {
											searchTerm += ' AND ';
										}
										searchTerm += '*' + extEscapeString(searchTermsArray[k]) + '*';
									}
								}
								if (searchTerm != "") {
									propValue = '(' + searchTerm + ')';
								}
							}
						}
						formQuery += (first ? '' : ' AND ') + escapeQName(propName) + ':' + propValue;
						first = false;
					}
				}
				else {
					// pseudo cm:content property - e.g. mimetype, size or encoding
					formQuery += (first ? '' : ' AND ') + 'cm:content.' + propName + ':"' + propValue + '"';
					first = false;
				}
			} else if (p.indexOf("assoc_") == 0 && p.lastIndexOf("_added") == p.length - 6) {
				//поиск по ассоциациям
				var assocName = p.substring(6);
				assocName = assocName.substring(0, assocName.lastIndexOf("_added"));
				if (assocName.indexOf("_") !== -1) {
					assocName = assocName.replace("_", ":") + "-ref"; //выведение имя свойства, в котором искать
					formQuery += (first ? '(' : ' AND (');
					var assocValues = propValue.split(",");
					var firstAssoc = true;
					for (var k = 0; k < assocValues.length; k++) {
						var assocValue = assocValues[k];
						if (!firstAssoc) {
							formQuery += " OR ";    //ищем по "или"
						}
						formQuery += escapeQName(assocName) + ':"*' + assocValue + '*"';
						firstAssoc = false;
					}
					formQuery += ") ";
					first = false;
				}
			}
		}
	}
	return formQuery;
}

function escapeString(str) {
	return str.replace(/"/g, '\\\"');
}

function escapeQName(qname) {
	var separator = qname.indexOf(':'),
		namespace = qname.substring(0, separator),
		localname = qname.substring(separator + 1);

	return extEscapeString(namespace) + ':' + extEscapeString(localname);
}

function extEscapeString(value) {
	var result = "";

	for (var i = 0, c; i < value.length; i++) {
		c = value.charAt(i);
		if (i == 0) {
			if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= 'а' && c <= 'я') || (c >= 'А' && c <= 'Я') || c == '_')) {
				result += '\\';
			}
		}
		else {
			if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= 'а' && c <= 'я') || (c >= 'А' && c <= 'Я') || c == '_' || c == '$' || c == '#')) {
				result += '\\';
			}
		}
		result += c;
	}
	return result;
}


function trimString(str) {
	return str.replace(/^\s+|\s+$/g, '');
}

function replaceAll(find, replace, str) {
	return str.replace(new RegExp(find, 'g'), replace);
}