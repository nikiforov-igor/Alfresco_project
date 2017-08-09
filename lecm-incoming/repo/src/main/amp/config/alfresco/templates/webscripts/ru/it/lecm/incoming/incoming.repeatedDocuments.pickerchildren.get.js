<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var filter = '@lecm-document-aspects\\:reg-data-is-registered:"true"';
		var document = search.findNode(args["documentRef"]);
		if (document) {
			var argCustomSearchTerm = args["customSearchTerm"];
            var argSearchMode = args["searchMode"];
			var searchTerm = null;
			if (argCustomSearchTerm) {
				var searchTermArr = argCustomSearchTerm.split("lecm-document:present-string:");
                searchTerm = searchTermArr && searchTermArr.length ? searchTermArr[1] : null;
			}
            var searchProp = null;
			if (searchTerm) {
				var searchPropArr = argCustomSearchTerm.split(searchTerm);
				searchProp = searchPropArr && searchPropArr.length ? searchPropArr[0] : null;
			}

            var startLength = filter.length,
            	searchPropNames = getSearchPropNamesArray();

            if (searchTerm) {
                filter += ' AND (' + '@' + searchProp.replace(":", "\\:") + '"*' + searchTerm + '*"';
            }
            searchPropNames.forEach(function (searchProp) {
				if (args[searchProp.elName] == "true") {
                    filter = addSimilarFilter(filter, document, searchProp.propName, startLength, argSearchMode);
				}
            });
            if (args["subject"] == "true") {
                var docSubjects = document.assocs["lecm-document:subject-assoc"];
                var docSubjectsFilter = "";
                if (docSubjects) {
                    for (var i = 0; i < docSubjects.length; i++) {
                        if (i != 0) {
                            docSubjectsFilter += " OR ";
                        }
                        docSubjectsFilter += '@lecm\\-document\\:subject\\-assoc\\-ref:"*' + docSubjects[i].nodeRef.toString() + '*"';
                    }
                }
                if (docSubjectsFilter.length) {
                    if (firstAddFilter) {
                        filter += " AND (" + docSubjectsFilter + ")";
                    } else {
                        filter += " OR (" + docSubjectsFilter + ")";
                    }
                }
            }
            filter += ")";
		}

	var data = getPickerChildrenItems(filter);

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = data.results;
	model.additionalProperties = data.additionalProperties;
};

function addSimilarFilter(filter, document, prop, startLength, searchMode) {
	var propValue = document.properties[prop];
	if (propValue != null) {
        if (filter.length > 0) {
            if (filter.length == startLength) {
                filter += " AND (";
            } else {
                if (searchMode == "at_least_one") {
                    filter += " OR ";
                } else {
                    filter += " AND ";
                }
            }
        }
        filter += '@' + prop.replace(":", "\\:") + ':"';
		if (prop == "lecm-incoming:outgoing-date") {
            filter += propValue.toISOString() + '"';
		} else {
            filter += propValue + '"';
		}
	}
	return filter;
}

function getSearchPropNamesArray () {
	var searchPropNamesArray = [];
    searchPropNamesArray.push({elName: "sender", propName: "lecm-incoming:sender-assoc-ref"});
    searchPropNamesArray.push({elName: "addressee", propName: "lecm-incoming:addressee-assoc-ref"});
    searchPropNamesArray.push({elName: "title", propName: "lecm-document:title"});
    searchPropNamesArray.push({elName: "outgoing_number", propName: "lecm-incoming:outgoing-number"});
    searchPropNamesArray.push({elName: "outgoing_date", propName: "lecm-incoming:outgoing-date"});
    searchPropNamesArray.push({elName: "subject", propName: "lecm-document:subject-assoc-ref"});
	return searchPropNamesArray;
}

main();