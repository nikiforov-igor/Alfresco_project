<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">

function getDuplicatesInfo (model, query) {
    var count = searchCounter.query({
        query: query,
        language: "fts-alfresco",
        onerror: "exception"
    });

    model.hasDuplicate = count > 0;
    model.duplicates = [];
    if (model.hasDuplicate) {
        var results = search.query({
            query: query,
            language: "fts-alfresco",
            onerror: "exception"
        });

        for (var i in results) {
            var result = results[i];
            if (result) {
                model.duplicates.push(result);
            }
        }
    }
};

function concatQuery(baseQuery, operator, field, value, notEscape, notQuoted) {
    if (value) {
        if (baseQuery) {
            baseQuery += (' ' + operator + ' ');
        }
        baseQuery += (field + ':' +  (notQuoted ? '' : '\"') + (notEscape ? value : escapeString("" + value)) + (notQuoted ? '' : '\"'));
    }
    return baseQuery;
}