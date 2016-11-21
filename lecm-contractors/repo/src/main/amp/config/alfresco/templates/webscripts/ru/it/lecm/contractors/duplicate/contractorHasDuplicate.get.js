function main() {
    var fullName = args["fullName"] ? args["fullName"] : null;
    var shortName = args["shortName"] ? args["shortName"] : null;
    var inn = args["inn"] ? args["inn"] : null;
    var kpp = args["kpp"] ? args["kpp"] : null;

    var query = 'TYPE:\"lecm-contractor:contractor-type\" AND NOT @lecm\\-dic\\:active:false AND (';
    if (fullName) {
        query += '=@lecm\\-contractor\\:fullname\\-search:\"' + contractorsRootObject.formatContractorName(fullName) + '\"';
    }
    if (shortName) {
        if (query.length > 0) {
            query += ' OR ';
        }
        query += '=@lecm\\-contractor\\:shortname\\-search:\"' + contractorsRootObject.formatContractorName(shortName) + '\"';
    }
    if (inn && inn.length > 0) {
        if (query.length > 0) {
            query += ' OR ';
        }
        query += '=@lecm\\-contractor\\:INN:\"' + _escapeString(inn) + '\"';
    }
    if (kpp && kpp.length > 0) {
        if (query.length > 0) {
            query += ' OR ';
        }
        query += '=@lecm\\-contractor\\:KPP:\"' + _escapeString(kpp) + '\"';
    }
    query += ')';

    if (args["nodeRef"]) {
        query += " AND NOT ID:\"" + args["nodeRef"] + "\"";
    }

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
            var contractor = results[i];
            if (contractor) {
                model.duplicates.push(contractor);
            }
        }
    }
}

function _escapeString(value) {
    var result = "";
    for (var i = 0, c; i < value.length; i++) {
        c = value.charAt(i);
        if (i == 0) {
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= 'а' && c <= 'я') || (c >= 'А' && c <= 'Я') || (c >= '0' && c <= '9') || c == '_')) {
                result += '\\';
            }
        }
        else {
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= 'а' && c <= 'я') || (c >= 'А' && c <= 'Я') || (c >= '0' && c <= '9') || c == '_' || c == '$' || c == '#')) {
                result += '\\';
            }
        }
        result += c;
    }
    return result;
}

main();