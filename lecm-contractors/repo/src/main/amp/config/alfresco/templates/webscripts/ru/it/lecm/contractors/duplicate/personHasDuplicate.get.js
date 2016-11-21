function main() {
    var lastName = args["lastName"] ? args["lastName"] : null;
    var firstName = args["firstName"] ? args["firstName"] : null;
    var middleName = args["middleName"] ? args["middleName"] : null;
    var region = args["region"] ? args["region"] : null;
    var inn = args["inn"] ? args["inn"] : null;
    var ogrn = args["ogrn"] ? args["ogrn"] : null;

    var query = 'TYPE:\"lecm-contractor:physical-person-type\" AND NOT @lecm\\-dic\\:active:false AND (';

    var subQueryByNames = '';

    if (lastName) {
        subQueryByNames += '=@lecm\\-contractor\\:lastName:\"' + lastName + '\"';
    }

    if (firstName) {
        if (subQueryByNames.length > 0) {
            subQueryByNames += ' AND ';
        }
        subQueryByNames += '=@lecm\\-contractor\\:firstName:\"' + firstName + '\"';
    }

    if (region) {
        if (subQueryByNames.length > 0) {
            subQueryByNames += ' AND ';
        }
        subQueryByNames += '@lecm\\-contractor\\:region\\-association\\-ref:\"' + region + '\"';
    }

    if (middleName) {
        if (subQueryByNames.length > 0) {
            subQueryByNames += ' AND ';
        }
        subQueryByNames += ('(=@lecm\\-contractor\\:middleName:\"' + middleName + '\" OR NOT @lecm\\-contractor\\:middleName:\"?*\")');
    }

    if (subQueryByNames.length > 0) {
        query += ('(' + subQueryByNames + ')');
    }

    if (inn && inn.length > 0) {
        if (query.length > 0) {
            query += ' OR ';
        }
        query += '=@lecm\\-contractor\\:INN:\"' + _escapeString(inn) + '\"';
    }
    if (ogrn && ogrn.length > 0) {
        if (query.length > 0) {
            query += ' OR ';
        }
        query += '=@lecm\\-contractor\\:OGRN:\"' + _escapeString(ogrn) + '\"';
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
            var person = results[i];
            if (person) {
                model.duplicates.push(person);
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