function getPickerChildrenItems(filter, doNotCheckAccess, isPost, itemParams)
{
	var argsFilterType = getArg('filterType', isPost, itemParams),
		argsSelectableType = getArg('selectableType', isPost, itemParams),
		argsSearchTerm = getArg('searchTerm', isPost, itemParams),
		argsAdditionalFilter = getArg('additionalFilter', isPost, itemParams),
		argsSkipCount = getArg('skipCount', isPost, itemParams),
		argsMaxResults = getArg('size', isPost, itemParams),
		argsXPath = getArg('xpath', isPost, itemParams),
		argsRootNode = getArg('rootNode', isPost, itemParams),
		argsNameSubstituteString = getArg('nameSubstituteString', isPost, itemParams),
		argsTitleNameSubstituteString = getArg('titleNameSubstituteString', isPost, itemParams),
		argsSelectedItemsNameSubstituteString = getArg('selectedItemsNameSubstituteString', isPost, itemParams) ? getArg('selectedItemsNameSubstituteString', isPost, itemParams) : argsNameSubstituteString,
		pathElements = url.service.split("/"),
		parent = null,
		rootNode = businessPlatform.getHomeRef(),
		results = [],
		categoryResults = null,
		resultObj = null,
		lastPathElement = null,
		argsXPathLocation = getArg('xPathLocation', isPost, itemParams),
		argsXPathRoot = getArg('xPathRoot', isPost, itemParams),
		showNotSelectable = getArg('showNotSelectableItems', isPost, itemParams),
		showFolders = getArg('showFolders', isPost, itemParams),
		docType = getArg('docType', isPost, itemParams),
        useOnlyInSameOrg = ("true" == getArg('onlyInSameOrg', isPost, itemParams)),
		doNotCheck = (!doNotCheckAccess || ("" + doNotCheckAccess) == "false") ?
			("true" == getArg('doNotCheckAccess', isPost, itemParams)) : doNotCheckAccess,
		sortProp = getArg('sortProp', isPost, itemParams) ? getArg('sortProp', isPost, itemParams) : "cm:name",
		additionalProperties = getArg('additionalProperties', isPost, itemParams),
		argsPathRoot = getArg('pathRoot', isPost, itemParams),
		argsPathNameSubstituteString = getArg('pathNameSubstituteString', isPost, itemParams),
		argsUseObjectDescription = ("true" == getArg('useObjectDescription', isPost, itemParams));
	if (additionalProperties) {
		additionalProperties = additionalProperties.split(',');
	}

	if (logger.isLoggingEnabled()) {
		logger.log("children type = " + url.templateArgs.type);
		logger.log("argsSelectableType = " + argsSelectableType);
		logger.log("argsFilterType = " + argsFilterType);
		logger.log("argsSearchTerm = " + argsSearchTerm);
		logger.log("argsSkipCount = " + argsSkipCount);
		logger.log("argsMaxResults = " + argsMaxResults);
		logger.log("argsXPath = " + argsXPath);
		logger.log("nameSubstituteString = " + argsNameSubstituteString);
		logger.log("argsSelectedItemsNameSubstituteString = " + argsSelectedItemsNameSubstituteString);
		logger.log("argsXPathLocation = " + argsXPathLocation);
		logger.log("argsXPathRoot = " + argsXPathRoot);
	}

	try {
		// construct the NodeRef from the URL
		var nodeRef = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;

		// determine if we need to resolve the parent NodeRef

		if (argsXPath) {
			// resolve the provided XPath to a NodeRef
			var nodes = search.xpathSearch(argsXPath);
			if (nodes.length) {
				nodeRef = String(nodes[0].nodeRef);
			}
		}
		if (argsXPathLocation) {
			var root = businessPlatform.getHomeRef();
			// resolve the root for XPath
			if (argsXPathRoot) {
				var node = resolveNode(argsXPathRoot);
				if (node) {
					root = node;
				}
			}
			if (argsXPathLocation == '{currentLocation}') {
				nodeRef = node.nodeRef.toString();
			} else {
				var nodes = root.childrenByXPath(argsXPathLocation);
				if (nodes.length) {
					nodeRef = String(nodes[0].nodeRef);
				}
			}
		}

		var skipCount = 0;
		if (argsSkipCount) {
			// force the argsMaxResults var to be treated as a number
			skipCount = parseInt(argsSkipCount, 10) || skipCount;
		}
		// default to max of 100 results
		var maxResults = 20;
		if (argsMaxResults) {
			// force the argsMaxResults var to be treated as a number
			maxResults = parseInt(argsMaxResults, 10) || maxResults;
		}

		// if the last path element is 'doclib' or 'siblings' find parent node
		if (pathElements.length) {
			lastPathElement = pathElements[pathElements.length - 1];

			if (logger.isLoggingEnabled()) {
				logger.log("lastPathElement = " + lastPathElement);
			}

			if (lastPathElement == "siblings") {
				// the provided nodeRef is the node we want the siblings of so get it's parent
				var node = search.findNode(nodeRef);
				if (node) {
					nodeRef = node.parent.nodeRef;
				} else {
					// if the provided node was not found default to companyhome
					nodeRef = "alfresco://company/home";
				}
			}
			else if (lastPathElement == "doclib") {
				// we want to find the document library for the nodeRef provided
				nodeRef = findDoclib(nodeRef);
			}
		}

		if (url.templateArgs.type == "node") {
			var childNodes = [];

			parent = resolveNode(nodeRef);
			if (!parent && !argsXPath) {
				status.setCode(status.STATUS_NOT_FOUND, "Not a valid nodeRef: '" + nodeRef + "'");
				return null;
			}
			if (argsRootNode){
				rootNode = resolveNode(argsRootNode) || businessPlatform.getHomeRef();
			}

			if (parent && !argsSearchTerm && !argsAdditionalFilter && !filter)  {
				var ignoreTypes = null;
                if (argsFilterType) {
                    if (logger.isLoggingEnabled()) {
                        logger.log("ignoring types = " + argsFilterType);
                    }
                    ignoreTypes = argsFilterType;
                }

                var childType = null;
                if (showNotSelectable != "true") { //включим фильтрацию по типам/аспектам
                    childType = argsSelectableType;
                }
                //параметры метода - родитель, тип элементов, игнорируемые типы, макс число результатов, сдвиг, поле для сортировки, направление сортировка, только активные, проверять ли доступ по организации
                childNodes = base.getChilds(parent, childType, ignoreTypes, maxResults, skipCount, sortProp, true, true, doNotCheck, useOnlyInSameOrg).page;
			} else {
				var parentXPath = null, query;
				if (parent) {
					parentXPath = parent.getQnamePath();
				} else if (argsXPath) {
					parentXPath = argsXPath;
				}

				if (!parentXPath) {
					status.setCode(status.STATUS_NOT_FOUND, "Not a valid parent xPath");
					return null;
				}

				if (argsSearchTerm) {
					query = getFilterParams(argsSearchTerm, parentXPath);
				}

                if (showNotSelectable != "true") { //включим фильтрацию по типам/аспектам
                    var selectableQuery = getItemSelectableQuery(argsSelectableType, showFolders);
                    if (selectableQuery) {
                        query = (query ? (query + ' AND (') : '(') + selectableQuery + ')';
                    }
                }

				if (argsAdditionalFilter) {
					query = addAdditionalFilter(query, "" + searchQueryProcessor.processQuery(argsAdditionalFilter));
				}

				if (filter) {
					query = addAdditionalFilter(query, filter);
				}

				if (!doNotCheck) {
					query = addAdditionalFilter(query, "{{IN_SAME_ORGANIZATION({strict:" + useOnlyInSameOrg + "})}}");
				}

                query = (query ? (query + ' AND ') : '') + "NOT @lecm\\-dic\\:active:false";

				// Query the nodes - passing in default sort and result limit parameters
				if (query) {
					var sort = [{
						column: "@" + sortProp,
						ascending: true
					}];
					if (argsSearchTerm && argsSearchTerm.length) {
						sort.splice(0, 0, {
							column: "score",
							ascending: false
						});
					}

					childNodes = search.query(
						{
							query: searchQueryProcessor.processQuery(query),
							language: "fts-alfresco",
							page: {
								skipCount: skipCount,
								maxItems: maxResults
							},
							sort: sort
						});
				}
			}

			// retrieve the children of this node

			// Ensure folders and folderlinks appear at the top of the list
			var containerResults = [],
				contentResults = [];

			for each (var result in childNodes)
			{
				if (result.isContainer || result.type == "{http://www.alfresco.org/model/application/1.0}folderlink") {
					resultObj =
					{
						item: result,
						selectable: isItemSelectable(result, argsSelectableType)
					};
					containerResults.push(resultObj);
				} else {
					// wrap result and determine if it is selectable in the UI
					resultObj =
					{
						item: result,
						selectable: isItemSelectable(result, argsSelectableType)
					};
					//проверку можно оставить, так как используется на данный момент в одном месте - при выборе логотипа организации
					// и в том месте ограничение по максимальному числу результатов остутсвует (=1000 - то есть все элементы на одном уровне репозитория)
					if (checkDocType(result, docType)) {
						contentResults.push(resultObj);
					}
				}

				if (argsUseObjectDescription) {
					resultObj.visibleName = substitude.getObjectDescription(result);
					resultObj.titleVisibleName = substitude.formatNodeTitle(result, argsTitleNameSubstituteString);
					resultObj.selectedVisibleName = resultObj.visibleName;
				} else {
					resultObj.visibleName = substitude.formatNodeTitle(result, argsNameSubstituteString);
					resultObj.titleVisibleName = substitude.formatNodeTitle(result, argsTitleNameSubstituteString);
					resultObj.selectedVisibleName = substitude.formatNodeTitle(result, argsSelectedItemsNameSubstituteString);
				}

				var path = "/";
				var simplePath = "/";

				if (argsPathRoot) {
					var rootNodes = search.xpathSearch(argsPathRoot);
					if (rootNodes.length) {
						var pathRoot = rootNodes[0];
						var temp = result.parent;
						while (temp && !temp.equals(pathRoot)) {
							var pathNodeName;
							if (argsPathNameSubstituteString) {
								if (argsPathNameSubstituteString.length) {
									pathNodeName = substitude.formatNodeTitle(temp, argsPathNameSubstituteString);
								} else {
									pathNodeName = substitude.getObjectDescription(temp);
								}
							} else {
								pathNodeName = temp.name
							}

							path = "/" + pathNodeName + path;
							simplePath = "/_" + simplePath;
							temp = temp.parent;
						}
					}
				}

				resultObj.path = path;
				resultObj.simplePath = simplePath;
			}
			if (argsUseObjectDescription && sortProp == "cm:name") {
				containerResults.sort(function (a, b) {
					return a.visibleName > b.visibleName;
				});
				contentResults.sort(function (a, b) {
					return a.visibleName > b.visibleName;
				});
			}
			results = containerResults.concat(contentResults);
		} else if (url.templateArgs.type == "category") {
			var catAspect = getArg('aspect', isPost, itemParams) ? getArg('aspect', isPost, itemParams) : "cm:generalclassifiable";

			// TODO: Better way of finding this
			var rootCategories = classification.getRootCategories(catAspect);
			if (rootCategories && rootCategories.length) {
				rootNode = rootCategories[0].parent;
				if (nodeRef == "alfresco://category/root") {
					parent = rootNode;
					categoryResults = classification.getRootCategories(catAspect);
				}
				else {
					parent = search.findNode(nodeRef);
					categoryResults = parent.children;
				}

				if (argsSearchTerm) {
					var filteredResults = [];
					for each(result in categoryResults){
						if (result.properties.name.indexOf(argsSearchTerm) == 0) {
							filteredResults.push(result);
						}
					}
					categoryResults = filteredResults.slice(0);
				}
				categoryResults.sort(sortByName);

				// make each result an object and indicate it is selectable in the UI
				for each (var result in categoryResults){
					results.push(
						{
							item: result,
							selectable: true
						});
				}
			}
		} else if (url.templateArgs.type == "authority") {
			if (argsSelectableType == "cm:person" || argsSelectableType.indexOf("cm:person") > 0) {
				findUsers(argsSearchTerm, maxResults, results);
			} else if (argsSelectableType == "cm:authorityContainer" || argsSelectableType.indexOf("cm:authorityContainer") > 0) {
				findGroups(argsSearchTerm, maxResults, results);
			} else {
				// combine groups and users
				findGroups(argsSearchTerm, maxResults, results);
				findUsers(argsSearchTerm, maxResults, results);
			}
		}

		if (logger.isLoggingEnabled()) {
			logger.log("Found " + results.length + " results");
		}
	} catch (e) {
		var msg = e.message;

		if (logger.isWarnLoggingEnabled()) {
			logger.warn(msg);
		}

		status.setCode(500, msg);

		return;
	}

	if (itemParams && itemParams.itemKey) {
		results.forEach(function(resultItem) {
			resultItem.itemKey = itemParams.itemKey;
		});
	}

	return {
		parent: parent,
		rootNode: rootNode,
		results: results,
		additionalProperties: additionalProperties
	}
}

function getArg(argName, isPost, itemParams) {
	var argValue;
	if (itemParams && itemParams.hasOwnProperty(argName)) {
		// Получение аргумента из объекта переметров элемента
		argValue = itemParams[argName];
	} else if (isPost && json.has(argName)) {
		argValue = json.get(argName);
	} else {
		// Получение аргумента для GET-запроса
		argValue = args[argName];
	}
	return argValue ? '' + argValue : null;
}

function getItemSelectableQuery(selectableType, showFolders) {
	var selectable = '', types, typesLength, type;

	if (selectableType) {
		types = selectableType.split(",");
		typesLength = types.length;
		for (var i = 0; i < typesLength; i++) {
			type = types[i];
			if (type.length) {
				var isAspect = base.isAspect(type);
				selectable += (selectable ? " OR " : "") + (isAspect ? "ASPECT:" : "TYPE:") + '"' + type + '"';
            }
        }
		if (selectable) {
            selectable = "(" + selectable + ")";
        }
    }

	//selectable = selectable + (selectable ? " OR (" : "(")
	//	+ ('((TYPE:"cm:folder" OR TYPE:"app:folderlink") AND NOT TYPE:"cm:systemfolder") AND ' + (showFolders == "true" ? 'ISNOTNULL:"cm:name"' : 'ISNULL:"cm:name"')) + ')';
    return selectable;
}

function isItemSelectable(node, selectableType) {
    var selectable = true;

    if (selectableType) {
        var types = selectableType.split(",");
        for (var i = 0; i < types.length; i++) {
            if (types[i].length) {
                selectable = node.isSubType(types[i]);
                if (!selectable) {
                    // the selectableType could also be an aspect,
                    // if the node has that aspect it is selectable
                    selectable = node.hasAspect(types[i]);
                }

                if (selectable) {
                    break;
                }
            }
        }
    }

    return selectable;
}

/* Sort the results by case-insensitive name */
function sortByName(a, b) {
	return (b.properties.name.toLowerCase() > a.properties.name.toLowerCase() ? -1 : 1);
}

function findUsers(searchTerm, maxResults, results) {
	var paging = utils.createPaging(maxResults, -1);
	var searchResults = groups.searchUsers(searchTerm, paging, "lastName");

	// create person object for each result
	for each(var user in searchResults) {
		if (logger.isLoggingEnabled()) {
			logger.log("found user = " + user.userName);
		}

		// add to results
		results.push(
			{
				item: createPersonResult(user.person),
				selectable: true
			});
	}
}

function findGroups(searchTerm, maxResults, results) {
	if (logger.isLoggingEnabled()) {
		logger.log("Finding groups matching pattern: " + searchTerm);
	}

	var paging = utils.createPaging(maxResults, 0);
	var searchResults = groups.getGroupsInZone(searchTerm, "APP.DEFAULT", paging, "displayName");
	for each(var group in searchResults) {
		if (logger.isLoggingEnabled()) {
			logger.log("found group = " + group.fullName);
		}

		// add to results
		results.push(
			{
				item: createGroupResult(group.groupNode),
				selectable: true
			});
	}

	// sort the groups by name alphabetically
	if (results.length) {
		results.sort(function (a, b) {
			return (a.item.properties.name < b.item.properties.name) ? -1 : (a.item.properties.name > b.item.properties.name) ? 1 : 0;
		});
	}
}

/**
 * Returns the nodeRef of the document library of the site the
 * given nodeRef is located within. If the nodeRef provided does
 * not live within a site "alfresco://company/home" is returned.
 *
 * @param nodeRef The node to find the document library for
 * @return The nodeRef of the doclib or "alfresco://company/home" if the node
 *         is not located within a site
 */
function findDoclib(nodeRef) {
	var resultNodeRef = "alfresco://company/home";

	// find the given node
	var node = search.findNode(nodeRef);
	if (node) {
		// get the name of the site
		var siteName = node.siteShortName;

		if (logger.isLoggingEnabled()) {
			logger.log("siteName = " + siteName);
		}

		// if the node is in a site find the document library node using an XPath search
		if (siteName) {
			var nodes = search.xpathSearch("/app:company_home/st:sites/cm:" + search.ISO9075Encode(siteName) + "/cm:documentLibrary");
			if (nodes.length) {
				// there should only be 1 result, get the first one
				resultNodeRef = String(nodes[0].nodeRef);
			}
		}
	}

	return resultNodeRef;
}

/**
 * Resolve "virtual" nodeRefs, nodeRefs and xpath expressions into nodes
 *
 * @method resolveNode
 * @param reference {string} "virtual" nodeRef, nodeRef or xpath expressions
 * @return {ScriptNode|null} Node corresponding to supplied expression. Returns null if node cannot be resolved.
 */
function resolveNode(reference) {
	var node = null;
	try {
		if (reference == "alfresco://company/home" || reference == "{companyhome}") {
			node = companyhome;
		} else if (reference == "alfresco://user/home") {
			node = userhome;
		} else if (reference == "alfresco://sites/home") {
			node = companyhome.childrenByXPath("st:sites")[0];
		} else if (reference.indexOf("://") > 0) {
			node = search.findNode(reference);
		} else if (reference.substring(0, 1) == "/") {
			node = search.xpathSearch(reference)[0];
		} else if (reference == "{organization}") {
			node = companyhome.childByNamePath("Организация");
		} else if (reference == "{lecmMyPrimaryUnit}") {
			node = orgstructure.getPrimaryOrgUnit(orgstructure.getCurrentEmployee());
		} else if (reference == "{lecmMyOrganization}") {
			node = orgstructure.getUnitByOrganization(orgstructure.getEmployeeOrganization(orgstructure.getCurrentEmployee()));
		}
	} catch (e) {
		if (logger.isWarnLoggingEnabled()) {
			logger.warn(e.message);
		}
		return null;
	}
	return node;
}

/**
 * Creates an Object representing the given person node.
 *
 * @method createPersonResult
 * @param node
 * @return Object representing the person
 */
function createPersonResult(node) {
	var personObject =
	{
		typeShort: node.typeShort,
		isContainer: false,
		properties: {},
		displayPath: node.displayPath,
		nodeRef: "" + node.nodeRef
	};

	// define properties for person
	personObject.properties.userName = node.properties.userName;
	personObject.properties.name = (node.properties.firstName ? node.properties.firstName + " " : "") +
		(node.properties.lastName ? node.properties.lastName : "") +
		" (" + node.properties.userName + ")";
	personObject.properties.jobtitle = (node.properties.jobtitle ? node.properties.jobtitle : "");

	return personObject;
}

/**
 * Creates an Object representing the given group node.
 *
 * @method createGroupResult
 * @param node
 * @return Object representing the group
 */
function createGroupResult(node) {
	var groupObject =
	{
		typeShort: node.typeShort,
		isContainer: false,
		properties: {},
		displayPath: node.displayPath,
		nodeRef: "" + node.nodeRef
	};

	// find most appropriate name for the group
	var name = node.properties.name;
	if (node.properties.authorityDisplayName && node.properties.authorityDisplayName.length) {
		name = node.properties.authorityDisplayName;
	}
	else if (node.properties.authorityName && node.properties.authorityName.length) {
		var authName = node.properties.authorityName;
		if (authName.indexOf("GROUP_") == 0) {
			name = authName.substring(6);
		} else {
			name = authName;
		}
	}

	// set the name
	groupObject.properties.name = name;

	return groupObject;
}

function trimString(str) {
  return str ? str.replace(/^\s+|\s+$/g, '') : '';
}

function escapeString(str) {
  return str ? str.replace(/"/g, '\\\"') : '';
}

function getFilterParams(filterData, parentXPath) {
	var query = " +PATH:\"" + parentXPath + "//*\"";
	var columns = [];
	if (filterData) {
		columns = filterData.split('#');
	}

	var params = "",
		or = " OR",
		ampersand = " @";
	for (var i = 0; i < columns.length; i++) {
		var namespace = columns[i].split(":");
		if (!columns[i + 1]) {
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

		params += ampersand + escapeQuery(namespace[0]) + "\\:" + escapeQuery(namespace[1]) + ":" + '(' + filter + ')' + or;
	}
	if (params) {
		query += " AND " + "(" + params + " )";
	}
	return query;
}

function addAdditionalFilter(query, additionalParameters) {
	if (additionalParameters
		&& 'ISNOTNULL:"sys:node-dbid"' != additionalParameters
		&& 'ISNOTNULL:"cm:name"' != additionalParameters) {

		var notSingleQueryPattern = /^NOT[\s]+.*(?=\sOR\s|\sAND\s|\s\+|\s\-)/i;
		var singleNotQuery = additionalParameters.indexOf("NOT") == 0 && !notSingleQueryPattern.test(additionalParameters);

		query += " AND " + (!singleNotQuery ? "(" : "")
			+ escapeQuery(additionalParameters) + (!singleNotQuery ? ")" : "");
	}
	return query;
}

function checkDocType(item, docType) {
	var result = false;
	if (!docType) {
		return true;
	}
	var extns =
	{
		"aep": "aep",
		"ai": "ai",
		"aiff": "aiff",
		"asf": "video",
		"asnd": "asnd",
		"asx": "video",
		"au": "audio",
		"avi": "video",
		"avx": "video",
		"bmp": "img",
		"css": "text",
		"divx": "video",
		"doc": "doc",
		"docx": "doc",
		"eml": "eml",
		"fla": "fla",
		"flv": "video",
		"fxp": "fxp",
		"gif": "img",
		"htm": "html",
		"html": "html",
		"indd": "indd",
		"jpeg": "img",
		"jpg": "img",
		"key": "key",
		"mkv": "video",
		"mov": "video",
		"movie": "video",
		"mp3": "mp3",
		"mp4": "video",
		"mpeg": "video",
		"mpeg2": "video",
		"mpv2": "video",
		"numbers": "numbers",
		"odg": "odg",
		"odp": "odp",
		"ods": "ods",
		"odt": "odt",
		"ogg": "video",
		"ogv": "video",
		"pages": "pages",
		"pdf": "pdf",
		"png": "img",
		"ppj": "ppj",
		"ppt": "ppt",
		"pptx": "ppt",
		"psd": "psd",
		"qt": "video",
		"rtf": "rtf",
		"snd": "audio",
		"spx": "audio",
		"svg": "img",
		"swf": "swf",
		"tiff": "img",
		"txt": "text",
		"wav": "audio",
		"webm": "video",
		"wmv": "video",
		"xls": "xls",
		"xlsx": "xls",
		"xml": "xml",
		"xvid": "video",
		"zip": "zip"
	};
	var extn = item.getName().substring(item.getName().lastIndexOf(".") + 1).toLowerCase();
	if (extn in extns)
	{
		result = extns[extn] == docType;
	}
	return result;
}

function getFilterForAvailableElement(availableElements) {
	var filter = "=@sys\\:node-uuid:\"00000000\-0000\-0000\-0000\-000000000000\"";
	if (availableElements && availableElements.length) {
		for (var i = 0; i < availableElements.length; i++) {
			filter += " OR =@sys\\:node-uuid:\"" + availableElements[i].nodeRef.getId() + "\"";
		}
	}
	return filter;
}

function escapeQuery(value) {
	var result = "";

	for (var i = 0, c, prev_c; i < value.length; i++) {
		c = value.charAt(i);
		prev_c = i > 0 ? value.charAt(i-1) : null;

		if (c == '-' && '\\' != prev_c && ' ' != prev_c) {
			result += '\\';
		}

		result += c;
	}
	return result;
}
