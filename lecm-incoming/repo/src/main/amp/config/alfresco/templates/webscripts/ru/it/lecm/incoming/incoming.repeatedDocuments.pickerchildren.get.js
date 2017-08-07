<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var filter = '@lecm-document-aspects\\:reg-data-is-registered:"true"';
		var document = search.findNode(args["documentRef"]);
		if (document) {
			var searchTerm = args["customSearchTerm"].split("lecm-document:present-string:")[1];
            var searchProp = args["customSearchTerm"].split(searchTerm)[0];
			if (searchTerm && args["atLeastOne"] == "false") {
				filter += ' AND ' + '@' + searchProp.replace(":", "\\:") + '"*' + searchTerm + '*"';
                var data = getPickerChildrenItems(filter);
			}
			if (args["atLeastOne"] == "true") {
				var firstAddFilter = true;
				if (searchTerm) {
                    filter += ' AND ' + '@' + searchProp.replace(":", "\\:") + '"*' + searchTerm + '*"';
                    firstAddFilter = false;
				}
				if (args["sender"] == "true") {
					filter = addSimilarFilter(filter, document, "lecm-incoming:sender-assoc-ref", firstAddFilter);
                    firstAddFilter = false;
				}
				if (args["addressee"] == "true") {
                    filter = addSimilarFilter(filter, document, "lecm-incoming:addressee-assoc-ref", firstAddFilter);
                    firstAddFilter = false;
				}
				if (args["title"] == "true") {
                    filter = addSimilarFilter(filter, document, "lecm-document:title", firstAddFilter);
                    firstAddFilter = false;
				}
				if (args["outgoing_number"] == "true") {
                    filter = addSimilarFilter(filter, document, "lecm-incoming:outgoing-number", firstAddFilter);
                    firstAddFilter = false;
				}
				if (args["outgoing_date"] == "true") {
                    filter = addSimilarFilter(filter, document, "lecm-incoming:outgoing-date", firstAddFilter);
                    firstAddFilter = false;
				}
				if (args["subject"] == "true") {
                    var docSubjects = document.assocs["lecm-document:subject-assoc"];
                    var docSubjectsFilter = "";
                    if (docSubjects) {
                        for (var i = 0; i < docSubjects.length; i++) {
                            if (i != 0) {
                                docSubjectsFilter += " OR ";
                            }
                            docSubjectsFilter += '@lecm\\-document\\:subject\\-assoc\\-ref:"*' +  docSubjects[i].nodeRef.toString() + '*"';
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
			}

		}

	var data = getPickerChildrenItems(filter);

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = data.results;
	model.additionalProperties = data.additionalProperties;
};

function addSimilarFilter(filter, document, prop, firstAddFilter) {
	var propValue = document.properties[prop];
	if (propValue != null) {
        if (filter.length > 0) {
            if (firstAddFilter) {
                filter += " AND ";
            } else {
                if (args["searchMode"] == 0) {
                    filter += " OR ";
                } else {
                    filter += " AND ";
                }
            }
        }
		if (prop == "lecm-incoming:outgoing-date") {
            filter += '@' + prop.replace(":", "\\:") + ':"' + propValue.toISOString() + '"';
		} else {
            filter += '@' + prop.replace(":", "\\:") + ':"' + propValue + '"';
		}
	}
	return filter;
}

main();