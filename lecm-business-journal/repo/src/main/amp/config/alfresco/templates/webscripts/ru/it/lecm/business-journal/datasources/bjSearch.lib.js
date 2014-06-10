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
        results.push(BJEvaluator.run(nodes[i], flds, nameSubstituteStrings == null ? null : nameSubstituteStrings.split(",")));
        added++;
    }
    var versionable = false;
    var hasPermission = true;
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
    var nodes = [];

    if (searchConfigString != null && searchConfigString.length() > 0) {
        var searchConfig = jsonUtils.toObject(searchConfigString);
        if (searchConfig.fullTextSearch != null && searchConfig.fullTextSearch.length > 0) {
            var fullTextSearch = searchConfig.fullTextSearch;
            var fullTextSearchJson = jsonUtils.toObject(fullTextSearch);
            if (fullTextSearchJson.searchTerm != null && fullTextSearchJson.searchTerm != "") {
                var value = fullTextSearchJson.searchTerm;
                var filterFields =  fullTextSearchJson.fields.split(",");
                var filter = [];
                for (var i in filterFields) {
                    filter[filterFields[i]] = value;
                }
                filter["lecm-busjournal:bjRecord-secondaryObj1-assoc-text-content"] = value;
                filter["lecm-busjournal:bjRecord-secondaryObj2-assoc-text-content"] = value;
                filter["lecm-busjournal:bjRecord-secondaryObj3-assoc-text-content"] = value;
                filter["lecm-busjournal:bjRecord-secondaryObj4-assoc-text-content"] = value;
                filter["lecm-busjournal:bjRecord-secondaryObj5-assoc-text-content"] = value;
                total = businessJournal.getRecordsCount(filter, false, showInactive);
                nodes = businessJournal.getRecords(sort, startIndex, pageSize, filter, false, showInactive);
            } else {
                if (searchConfig.formData != null && searchConfig.formData.length > 0) {
                    var formData = searchConfig.formData;
                    if (formData != null && formData.length > 0) {
                        var filter = [];
                        var formJson = jsonUtils.toObject(formData);
                        for (var key in formJson) {
                            if (key.indexOf("prop_") == 0 || key.indexOf("assoc_") == 0) {
                                var field = key.replace("prop_", "").replace("assoc_").replace("_", ":");
                                if (field != "") {
                                    filter[field] = formJson[key];
                                }
                            }
                        }
                        total = businessJournal.getRecordsCount(filter, true, showInactive);
                        nodes = businessJournal.getRecords(sort, startIndex, pageSize, filter, true, showInactive);
                    }
                }
            }
        } else {
            total = businessJournal.getRecordsCount(null, false, showInactive);
            nodes = businessJournal.getRecords(sort, startIndex, pageSize, null, false, showInactive);
        }
    } else {
        total = businessJournal.getRecordsCount(null, false, showInactive);
        nodes = businessJournal.getRecords(sort, startIndex, pageSize, null, false, showInactive);
    }
    return processResults(nodes, fields, nameSubstituteStrings, startIndex, total);
}