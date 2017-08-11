<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var filter = '@lecm-document-aspects\\:reg-data-is-registered:"true"';
		var document = search.findNode(args["documentRef"]);
		if (document) {
			var argCustomSearchTerm = args["customSearchTerm"];
            var argSearchMode = args["searchMode"],
                argSubject = args["subject"];

            var searchPropNamesArray = [
                {elName: "sender", propName: "lecm-incoming:sender-assoc-ref"},
                {elName: "addressee", propName: "lecm-incoming:addressee-assoc-ref"},
                {elName: "title", propName: "lecm-document:title"},
                {elName: "outgoing_number", propName: "lecm-incoming:outgoing-number"},
                {elName: "outgoing_date", propName: "lecm-incoming:outgoing-date"}];

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

            var startLength = filter.length;

            if (searchTerm) {
                filter += ' AND (' + '@' + searchProp.replace(":", "\\:") + '"*' + searchTerm + '*"';
            }
            searchPropNamesArray.forEach(function (searchProp) {
				if (args[searchProp.elName] == "true") {
                    filter = addSimilarFilter(filter, document, searchProp.propName, startLength, argSearchMode);
				}
            });
            if (argSubject == "true") {
                var docSubjects = document.assocs["lecm-document:subject-assoc"];
                var docSubjectsFilter = "";
                if (docSubjects) {
                    for (var i = 0; i < docSubjects.length; i++) {
                        if (i != 0) {
                            docSubjectsFilter += " AND ";
                        }
                        docSubjectsFilter += '@lecm\\-document\\:subject\\-assoc\\-ref:"*' + docSubjects[i].nodeRef.toString() + '*"';
                    }
                }
                if (docSubjectsFilter.length) {
                    if (filter.length == startLength) {
                        filter += " AND ((" + docSubjectsFilter + ")";
                    } else {
                        if (argSearchMode == "at_least_one") {
                            filter += " OR (";
                        } else {
                            filter += " AND (";
                        }
                        filter += docSubjectsFilter + ")";
                    }
                }
            }
            if (filter.length > startLength) {
                filter += ")";
            }
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

main();