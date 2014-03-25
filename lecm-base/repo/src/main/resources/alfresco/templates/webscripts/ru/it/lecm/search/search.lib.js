/**
 * Search Component
 *
 * Takes the following object as Input:
 *    params
 *    {
 *       term: search terms
 *       query: advanced search query json (search by form attributes)
 *       fields: returning fields
 *       filter: addition filter
 *       sort: sort parameter
 *       maxResults: maximum results to return
 *    };
 *
 * Outputs:
 versionable: node is versionable,
 userAccess:{
 create: permission on create
 },
 paging:{
 totalRecords:number of results,
 startIndex:start index for paging
 },
 items: Array of objects containing the search results
 */

/**
 * Processes the search results. Filters out unnecessary nodes
 *
 * @return the final search results object
 */
function processResults(nodes, fields, nameSubstituteStrings, startIndex, total) {
    // empty cache state
    processedCache = {};
    var results = [],
        added = 0,
        item,
        failed = 0,
        i, j;

    if (logger.isLoggingEnabled())
        logger.log("Processing resultset of length: " + nodes.length);

    var flds = [];
    if (fields != null) {
	    if (fields.indexOf("cm_versionLabel") == -1) {
		    fields = fields + ",cm_versionLabel";
	    }
        if (fields.indexOf("lecm-dic_active") == -1) {
            fields = fields + ",lecm-dic_active";
        }
        var arrayFields = fields.split(","),
            numFields = arrayFields.length;

        for (var count = 0; count < numFields; count++) {
            var field = arrayFields[count];
            var index = field.indexOf("_");
            var part1 = field.substring(0, index);
            var part2 = field.substring(index + 1);
            flds.push(part1 + ":" + part2); // do not need escape
        }
    }

    for (i = 0, j = nodes.length; i < j; i++) {
        results.push(Evaluator.run(nodes[i], flds, nameSubstituteStrings == null ? null : nameSubstituteStrings.split(",")));
        added++;
    }
    var versionable = false;
    var hasPermission = true;
    if (nodes.length > 0) {
        if (nodes[0].hasAspect("cm:versionable")) {
            versionable = true;
        }
        hasPermission = nodes[0].hasPermission("CreateChildren");
    }

    if (logger.isLoggingEnabled())
        logger.log("Filtered resultset to length: " + results.length + ". Discarded item count: " + failed);

    return (
    {
        versionable:versionable,
        userAccess:{
            create:hasPermission
        },
        paging:{
            totalRecords:total,
            startIndex:startIndex
        },
        items:results
    });
}

/**
 * Helper to escape the QName string so it is valid inside an fts-alfresco query.
 * The language supports the SQL92 identifier standard.
 *
 * @param qname   The QName string to escape
 * @return escaped string
 */
function escapeQName(qname) {
    var separator = qname.indexOf(':'),
        namespace = qname.substring(0, separator),
        localname = qname.substring(separator + 1);

    return escapeString(namespace) + ':' + escapeString(localname);
}

function escapeString(value) {
    var result = "";

    for (var i = 0, c; i < value.length; i++) {
        c = value.charAt(i);
        if (i == 0) {
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_')) {
                result += '\\';
            }
        }
        else {
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || c == '$' || c == '#')) {
                result += '\\';
            }
        }
        result += c;
    }
    return result;
}

function getFiltersQuery(filters) {
    var resQuery = "";
    for (var index in filters) {
        var filter = filters[index];
        if (filter.query != null && ("" + filter.query).length > 0) {
            resQuery = resQuery + "(" + filter.query + ") AND ";
        }
    }
    resQuery =  resQuery.length > 5 ? resQuery.substring(0, resQuery.length - 5) : resQuery;

    return resQuery.length > 0 ? ("(" + resQuery + ")") : resQuery;
}

/**
 * Return Search results with the given search terms.
 *
 * "or" is the default operator, AND and NOT are also supported - as is any other valid fts-alfresco
 * elements such as "quoted terms" and (bracket terms) and also propname:propvalue syntax.
 *
 * @param params  Object containing search parameters - see API description above
 */
function getSearchResults(params) {
    var nodes,
        ftsQuery = "",
        searchConfigString = params.searchConfig,
        fields = params.fields,
	    nameSubstituteStrings = params.nameSubstituteStrings,
        showInactive = params.showInactive,
        parent = params.parent,
	    searchNodes = params.searchNodes,
        itemType = params.itemType,
        startIndex = params.startIndex,
        pageSize = params.maxResults,
        sort = params.sort,
        filterStr = params.filter;

    var total = 0;

    // sort field - expecting field to in one of the following formats:
    //  - short QName form such as: cm:name
    //  - pseudo cm:content field starting with "." such as: .size
    //  - any other directly supported search field such as: TYPE
    var sortField = {column:"cm:name", ascending:true};

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

    var filterObjs = [];
    if (filterStr != null && ("" + filterStr).length > 0) {
        var filtersJSON = eval('(' + filterStr.toString() + ')');
        for (var index in filtersJSON) {
            filterObjs.push(Filters.getFilterParams(filtersJSON[index]));
        }
    }

    // Advanced search form data search.
    // Supplied as json in the standard Alfresco Forms data structure:
    //    prop_<name>:value|assoc_<name>:value
    //    name = namespace_propertyname|pseudopropertyname
    //    value = string value - comma separated for multi-value, no escaping yet!
    // - underscore represents colon character in name
    // - pseudo property is one of any cm:content url property: mimetype|encoding|size
    // - always string values - interogate DD for type data
    if (searchConfigString != null && searchConfigString.length() > 0) {
        var searchConfig = jsonUtils.toObject(searchConfigString);
        var formData = searchConfig.formData;
        if (formData != null && formData.length > 0) {
            var formQuery = "",
                formJson = jsonUtils.toObject(formData);

            itemType = formJson.datatype;

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
                                        to = (sepindex === propValue.length - 1 ? "MAX" : propValue.substr(sepindex + 1, sepindex + 10));
                                    }
                                    else {
                                        // simple range found
                                        propName = propName.substr(0, propName.length - "-range".length);

                                        // work out if "min" and/or "max" are specified - use MIN and MAX otherwise
                                        from = (sepindex === 0 ? "MIN" : propValue.substr(0, sepindex));
                                        to = (sepindex === propValue.length - 1 ? "MAX" : propValue.substr(sepindex + 1));
                                    }
                                    formQuery += (first ? '' : ' AND ') + escapeQName(propName) + ':"' + from + '".."' + to + '"';
                                    first = false;
                                }
                            }
                            else {
                                formQuery += (first ? '' : ' AND ') + escapeQName(propName) + ':"*' + propValue + '*"';
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
			                    formQuery += escapeQName(assocName) + ':"' + assocValue + '"';
			                    firstAssoc = false;
		                    }
		                    formQuery += ") ";
		                    first = false;
	                    }
                    }
                }
            }
        }

        var fullTextSearchQuery = "";
        if (searchConfig.fullTextSearch != null && searchConfig.fullTextSearch.length > 0) {
            var fullTextSearch = searchConfig.fullTextSearch;
            var fullTextSearchJson = jsonUtils.toObject(fullTextSearch);

            var parentNodeRef = fullTextSearchJson["parentNodeRef"];
            logger.log("parentNodeRef = " + parentNodeRef);
            if (parentNodeRef != null && parentNodeRef.length > 0) {
                var parentNode = search.findNode(parentNodeRef);
                if (parentNode != null && (!searchConfig.filter || searchConfig.filter && searchConfig.filter.indexOf('PATH') == -1)) {
                    var xpath = parentNode.getQnamePath();
                    fullTextSearchQuery += " +PATH:\"" + xpath + "//*\"";
                }
            }

            var ftsFields = fullTextSearchJson["fields"];
            logger.log("ftsFields = " + ftsFields);
            var searchTerm = fullTextSearchJson["searchTerm"];
            logger.log("searchTerm = " + searchTerm);
            if (ftsFields != null && ftsFields.length > 0 && searchTerm != null && searchTerm.length > 0) {
                var columns = ftsFields.split(",");
                var fieldsQuery = "";

                for (var i = 0; i < columns.length; i++) {
                    fieldsQuery += this.escapeQName(columns[i]) + ':"*' + searchTerm + '*" OR ';
                }
                if (fieldsQuery.length > 5) {
                    fieldsQuery = fieldsQuery.substring(0, fieldsQuery.length - 4);
                    fullTextSearchQuery += (fullTextSearchQuery.length > 0 ? " AND " : "") + " (" + fieldsQuery + ")";
                }
            }
        }

        logger.log("fullTextSearchQuery = " + fullTextSearchQuery);
        var filter = searchConfig.filter;
        if (formQuery.length > 0 || ftsQuery.length > 0 || formData.length > 0 || fullTextSearchQuery.length > 0) {
            // extract data type for this search - advanced search query is type specific
            var typesQuery = "";

            if (itemType != null && itemType.length > 0) {
                var typesArray = itemType.split(",");
                var numTypes = typesArray.length;

                for (var count = 0; count < numTypes; count++) {
                    var type = typesArray[count];
                    if (type != null && type.length > 0) {
                        if (typesQuery.length > 0) {
                            typesQuery += " ";
                        }
                        typesQuery += '+TYPE:"' + type + '"'
                    }
                }
            }
            ftsQuery = (typesQuery.length !== 0 ? '(' + typesQuery + ')' : '(+TYPE:"cm:content" +TYPE:"cm:folder")') +
                (formQuery.length !== 0 ? ' AND (' + formQuery + ')' : '') +
                (filter != null && filter.length > 0 ? ' AND (' + filter + ')' : '') +
                (ftsQuery.length !== 0 ? ' AND (' + ftsQuery + ')' : '') +
                (fullTextSearchQuery.length !== 0 ? ' AND (' + fullTextSearchQuery + ')' : '');

            var filtersQuery = filterObjs != null && filterObjs.length > 0 ? getFiltersQuery(filterObjs) : null;
            if (filtersQuery && filtersQuery.length > 0) {
                ftsQuery += (' AND ' + filtersQuery);
            }
        }

        if (ftsQuery.length !== 0) {
            //whether show removed dictionaries
            if (!showInactive) {
                ftsQuery += ' AND (NOT (ASPECT:"lecm-dic:aspect_active") OR ' + this.escapeQName("lecm-dic:active") + ':true)';
            }

            //фильтр по родителю
            // TODO последняя проверка - временное решение для поиска по вложенным элементам. Нужно ввести доп параметр
            if (parent != null && parent.length() > 0 && ftsQuery.indexOf("PATH") < 0) {
                ftsQuery += ' AND PARENT:"' + parent + '"';
            }

	        //фильтр по доступным нодам
	        if(searchNodes != null) {
		        var query = "";
                for (i = 0; i < searchNodes.length; i++) {
	                query += "ID:" + searchNodes[i].replace(":", "\\:");
	                if (i < searchNodes.length - 1) {
		                query += " OR "
	                }
                }
		        ftsQuery += " AND (" + query + ")";
	        }

            if (ftsQuery.indexOf("#current-user") >= 0) {
                var employeeRef = orgstructure.getCurrentEmployee().getNodeRef().toString();
                ftsQuery = ftsQuery.split("#current-user").join(employeeRef);
            }

            if (ftsQuery.indexOf("#current-date") >= 0) {
                var curDate = new Date();
                var nDays = notifications.getSettingsNDays();
                var limitDate = workCalendar.getNextWorkingDate(new Date(), nDays);
                ftsQuery = ftsQuery.split("#current-date").join(base.dateToISOString(limitDate));
            }

            if (logger.isLoggingEnabled())
                logger.log("Query:\r\n" + ftsQuery + "\r\nSortby: " + (sort != null ? sort : ""));

            var sortColumns = [];
            if (sortField.column.charAt(0) == '.') {
                // handle pseudo cm:content fields
                sortField.column = "@{http://www.alfresco.org/model/content/1.0}content" + sortField.column;
            }
            else if (sortField.column.indexOf(":") != -1) {
                // handle attribute field sort
                sortField.column = "@" + utils.longQName(sortField.column);
            }
            sortColumns.push(sortField);

            // Получаем число всех записей
            var queryDef = {
                query:ftsQuery,
                language:"fts-alfresco",
                onerror:"no-results"
            };
            total = searchCounter.query(queryDef);

            // выполняем запрос с ограничением
            queryDef = {
                query:ftsQuery,
                language:"fts-alfresco",
                page:{maxItems:pageSize, skipCount:(startIndex < total ? startIndex : 0)},
                onerror:"no-results",
                sort:sortColumns
            };
            nodes = search.query(queryDef);
        }
    } else if(searchNodes != null) {
	    nodes = [];
	    var allNodes = [];
	    for (i = 0; i < searchNodes.length; i++) {
		    var searchNode = search.findNode(searchNodes[i]);
		    var active = searchNode.properties["lecm-dic:active"];
		    if (showInactive || (active == null || active)) {
			    allNodes.push(searchNode);
		    }
	    }
	    if (allNodes != null) {
		    allNodes.sort(function(a,b){
			    var value1 = a.properties[sortField.column];
			    var value2 = b.properties[sortField.column];

			    if (value1 < value2) {
					return -1;
			    } else if (value1 > value2){
				    return 1;
			    } else {
				    return 0;
			    }
		    });
		    if (!sortField.ascending) {
			    allNodes.reverse();
		    }

		    var endIndex = startIndex + pageSize;
		    if (endIndex > allNodes.length){
			    endIndex = allNodes.length;
		    }

		    for (i = startIndex; i < endIndex; i++) {
			    nodes.push(allNodes[i]);
		    }
		}
    } else { // ищем не используя SOLR
        nodes = [];
        if (parent != null && parent.length() > 0 && parent != 'NOT_LOAD') {
            var node = search.findNode(parent);
            if (node) {
                if (logger.isLoggingEnabled())
                 logger.log("Get childs for node:\r\n" + parent + "\r\nItemType: " + (itemType != null ? itemType : ""));
                 var childsPaged = base.getChilds(node, itemType, pageSize, startIndex, utils.shortQName(sortField.column), sortField.ascending, !showInactive);
                 nodes = childsPaged.page;
                 total = childsPaged.totalResultCountUpper;
                 if (logger.isLoggingEnabled())
                 logger.log("[Results]Found:\r\n" + nodes.length + "\r\nTotal: " + (total != null ? total : ""));
            }
        }
    }

    return processResults(nodes, fields, nameSubstituteStrings, startIndex, total);
}