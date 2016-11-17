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
            flds.push(field); // do not need escape
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
        versionable: versionable,
        userAccess: {
            create: hasPermission
        },
        paging: {
            totalRecords: total,
            startIndex: startIndex
        },
        items: results
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

function getFiltersQuery(filters) {
    var resQuery = "";
    for (var index in filters) {
        var filter = filters[index];
        if (filter.query != null && ("" + filter.query).length > 0) {
            resQuery = resQuery + "(" + filter.query + ") AND ";
        }
    }
    return resQuery.length > 5 ? resQuery.substring(0, resQuery.length - 5) : resQuery;
}

function getSearchQuery(params) {
    var
        ftsQuery = "",
        searchConfigString = params.searchConfig,
        showInactive = params.showInactive,
        useFilterByOrg = params.useFilterByOrg != null ? (("" + params.useFilterByOrg) == "true") : true,
        useOnlyInSameOrg = params.useOnlyInSameOrg  != null ? (("" + params.useOnlyInSameOrg) == "true") : false,
        parent = params.parent,
        searchNodes = params.searchNodes,
        itemType = params.itemType,
        filterStr = params.filter;

		var typesQuery = "", formQuery = "", fullTextSearchQuery = "", filter = "", filtersQuery = "";
		// вытаскивае6м инфу из searchConfig
		if (searchConfigString != null && searchConfigString.length() > 0) {
			var searchConfig = jsonUtils.toObject(searchConfigString);

			// данные из формы атрибутивного поиска
			var formData = searchConfig.formData;
			if (formData != null && formData.length > 0) {
				var formJson = jsonUtils.toObject(formData);
				itemType = formJson.datatype; // заменяем пришедший тип на тип из формы

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
								}
                                else {
                                    var propNamePrefix = '';
                                    if ((propName.match("-strong-constant-upper-case$") == "-strong-constant-upper-case")) {
                                        propName = propName.substr(0, propName.length - "-strong-constant-upper-case".length);
                                        propValue = '"' + propValue.toUpperCase() + '"';
                                        propNamePrefix = '=';
                                    } else if (propName.match("-strong-constant$") == "-strong-constant") {
                                        propName = propName.substr(0, propName.length - "-strong-constant".length);
                                        propValue = '"' + propValue + '"';
                                    } else {
                                        if (propValue != null && propValue != "") {
                                            var searchTermsArray = propValue.split(" ");
                                            searchTerm = "";
                                            for (var k = 0; k < searchTermsArray.length; k++) {
                                                var newSearchTerm = searchTermsArray[k];
                                                if (newSearchTerm != null && newSearchTerm != "") {
                                                    if (k > 0) {
                                                        searchTerm += ' AND ';
                                                    }
                                                    searchTerm += '*' + escapeString(searchTermsArray[k]) + '*';
                                                }
                                            }
                                            if (searchTerm != "") {
                                                propValue = '(' + searchTerm + ')';
                                            }
                                        }
                                    }
                                    formQuery += (first ? '' : ' AND ') + propNamePrefix + escapeQName(propName) + ':' + propValue;
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
						}  else if (p.indexOf("ID") == 0 && p.lastIndexOf("_added") == p.length - 6) {
                            //поиск по ассоциациям
                            var idName = p.substring(0, p.lastIndexOf("_added"));
                            formQuery += (first ? '(' : ' AND (');
                            var idValues = propValue.split(",");
                            var addOR = false;
                            for (var j = 0; j < idValues.length; j++) {
                                var idValue = idValues[j];
                                if (addOR) {
                                    formQuery += " OR ";
						}
                                formQuery += (idName + ':"' + idValue + '"');
                                addOR = true;
					}
                            formQuery += ") ";
                            first = false;
				}
			}
				}
			}

			//полнотекстовый поиск
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
                    var searchTermsArray = searchTerm.split(" ");
                    searchTerm = "";
                    for (var k = 0; k < searchTermsArray.length; k++) {
                        var newSearchTerm = searchTermsArray[k];
                        if (newSearchTerm != null && newSearchTerm != "") {
                            if (k > 0) {
                                searchTerm += ' AND ';
                            }
                            searchTerm += '*' + searchTermsArray[k] + '*';
                        }
                    }
					var columns = ftsFields.split(",");
					var fieldsQuery = "";

                    if (searchTerm != "") {
                        for (var i = 0; i < columns.length; i++) {
                            fieldsQuery += this.escapeQName(columns[i]) + ':(' + searchTerm + ') OR ';
                        }
                    }
					if (fieldsQuery.length > 5) {
						fieldsQuery = fieldsQuery.substring(0, fieldsQuery.length - 4);
						fullTextSearchQuery += (fullTextSearchQuery.length > 0 ? " AND " : "") + " (" + fieldsQuery + ")";
					}
				}
			}

			logger.log("fullTextSearchQuery = " + fullTextSearchQuery);

			// фильтры
			filter = searchConfig.filter != null ? searchConfig.filter : '';
			var filterObjs = [];
			if (filterStr != null && ("" + filterStr).length > 0) {
				var filtersJSON = eval('(' + filterStr.toString() + ')');
				for (var index in filtersJSON) {
					filterObjs.push(Filters.getFilterParams(filtersJSON[index]));
				}
			}
			filtersQuery = filterObjs != null && filterObjs.length > 0 ? getFiltersQuery(filterObjs) : '';

			logger.log("filtersQuery = " + filtersQuery);
		}

		// extract data type for this search - advanced search query is type specific
		if (itemType != null && ("" + itemType).length > 0) {
			var typesArray = itemType.split(",");
			var numTypes = typesArray.length;

			for (var count = 0; count < numTypes; count++) {
				var type = typesArray[count];
				if (type != null && ("" + type).length > 0) {
					if (typesQuery.length > 0) {
						typesQuery += " ";
					}
					typesQuery += '+TYPE:"' + type + '"'
				}
			}
		}

		ftsQuery += (typesQuery.length !== 0 ? '(' + typesQuery + ')' : '');
		ftsQuery += (formQuery.length !== 0 ? ((ftsQuery.length !== 0 ? ' AND' : '') + '(' + formQuery + ')') : '');

        var notSingleQueryPattern = /^NOT[\s]+.*(?=\sOR\s|\sAND\s|\s\+|\s\-)/i;
        var singleNotQuery = false;

        if (filter.length !== 0) {
            singleNotQuery = filter.indexOf("NOT") == 0 && !notSingleQueryPattern.test(filter);
            ftsQuery += ((ftsQuery.length !== 0 ? ' AND ' : '') + (!singleNotQuery ? '(' : '') + filter + (!singleNotQuery ? ')' : ''));
        }

        if (filtersQuery.length !== 0 ) {
            singleNotQuery = !(filtersQuery.indexOf("NOT") == 0 || !notSingleQueryPattern.test(filtersQuery));
            ftsQuery += ((ftsQuery.length !== 0 ? ' AND ' : '') + (!singleNotQuery ? '(' : '') + filtersQuery + (!singleNotQuery ? ')' : ''));
        }
		ftsQuery += (fullTextSearchQuery.length !== 0 ? ((ftsQuery.length !== 0 ? ' AND ' : '') + '(' + fullTextSearchQuery + ')') : '');

		//фильтр по родителю
		// TODO последняя проверка - временное решение для поиска по вложенным элементам. Нужно ввести доп параметр
		if (parent != null && ("" + parent).length > 0 && (ftsQuery.length == 0 || ftsQuery.indexOf("PATH") < 0)) {
			ftsQuery += (ftsQuery.length !== 0 ? ' AND' : '') + ' PARENT:"' + parent + '"';
		}

		// по активности
		if (!showInactive) {
			ftsQuery += (ftsQuery.length !== 0 ? ' AND ' : '') + 'NOT @' + this.escapeQName("lecm-dic:active") + ':false';
		}

        // по организации
        if (useFilterByOrg) {
            ftsQuery += (ftsQuery.length !== 0 ? ' AND ' : '') + '{{IN_SAME_ORGANIZATION({strict:' + useOnlyInSameOrg + '})}}';
        }
		//фильтр по доступным нодам
		if (searchNodes != null) {
			ftsQuery = "";
			for (i = 0; i < searchNodes.length; i++) {
				ftsQuery += "ID:" + searchNodes[i].replace(":", "\\:");
				if (i < searchNodes.length - 1) {
					ftsQuery += " OR "
				}
			}
		}
	return ftsQuery;
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
		fields = params.fields,
		nameSubstituteStrings = params.nameSubstituteStrings,
        useFilterByOrg = params.useFilterByOrg != null ? (("" + params.useFilterByOrg) == "true") : true,
        useOnlyInSameOrg = params.useOnlyInSameOrg  != null ? (("" + params.useOnlyInSameOrg) == "true") : false,
		showInactive = params.showInactive,
		parent = params.parent,
		itemType = params.itemType,
		startIndex = params.startIndex,
		pageSize = params.maxResults,
		useChildQuery = params.useChildQuery,
		sort = params.sort;

	var total = 0;

	//настройки сортировки
	// sort field - expecting field to in one of the following formats:
	//  - short QName form such as: cm:name
	//  - pseudo cm:content field starting with "." such as: .size
	//  - any other directly supported search field such as: TYPE
	var sortField = {column: "cm:name", ascending: true};
	if (sort != null && ("" + sort).length > 0) {
		sort = '' + sort;
		var asc = true;
		var separator = sort.indexOf("|");
		if (separator != -1) {
			asc = (sort.substring(separator + 1) == "true");
			sort = sort.substring(0, separator);
		}
		sortField.column = sort;
		sortField.ascending = asc;
	}

	if (!useChildQuery) {
        // обработка запроса процессорами
		var ftsQuery = getSearchQuery(params);
        ftsQuery = searchQueryProcessor.processQuery(ftsQuery);

        if (logger.isLoggingEnabled())
            logger.log("Query:\r\n" + ftsQuery + "\r\nSortby: " + (sort != null ? sort : ""));

        // Получаем число всех записей
        var queryDef = {
            query: ftsQuery,
            language: "fts-alfresco",
            onerror: "no-results"
        };
        total = searchCounter.query(queryDef, false, false);

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

        // выполняем запрос с ограничением
        queryDef = {
            query: ftsQuery,
            language: "fts-alfresco",
            page: {maxItems: pageSize, skipCount: (startIndex < total ? startIndex : 0)},
            onerror: "no-results",
            sort: sortColumns
        };
        nodes = search.query(queryDef);
    } else { // ищем не используя SOLR
        nodes = [];
        if (parent != null && parent.length() > 0 && parent != 'NOT_LOAD') {
            var node = search.findNode(parent);
            if (node) {
                if (logger.isLoggingEnabled())
                    logger.log("Get childs for node:\r\n" + parent + "\r\nItemType: " + (itemType != null ? itemType : ""));
                var childsPaged = base.getChilds(node.nodeRef, itemType, null, pageSize, startIndex, utils.shortQName(sortField.column), sortField.ascending, !showInactive, !useFilterByOrg, useOnlyInSameOrg);
                nodes = childsPaged.page;
                total = childsPaged.totalResultCountUpper;
                if (logger.isLoggingEnabled())
                    logger.log("[Results]Found:\r\n" + nodes.length + "\r\nTotal: " + (total != null ? total : ""));
            }
        }
    }

    return processResults(nodes, fields, nameSubstituteStrings, startIndex, total);
}

/**
 * Вспомогательный метод для сортировки нод по указанному свойству
 * @param list список нод для сортировки
 * @param sortField свойство, по которому сортировать
 * @param sortAsc направление сортировки
 * @returns {*}
 */
function sortResults(list, sortField, sortAsc) {
    if (list == null || sortField == null) {
        return list;
    }
    list.sort(function (a, b) {
        var value1 = a.properties[sortField];
        var value2 = b.properties[sortField];
        if (value1 == null) {
            if (value2 != null) {
                return -1;
            } else {
                return 0;
            }
        } else if (value2 == null) {
            return 1;
        } else if (value1 < value2) {
            return -1;
        } else if (value1 > value2) {
            return 1;
        } else {
            return 0;
        }
    });
    if (!sortAsc) {
        list.reverse();
    }
    return list;
}
