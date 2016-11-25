<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/contractors/duplicate/duplicate.lib.js">

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
        query += '=@lecm\\-contractor\\:INN:\"' + escapeString(inn) + '\"';
    }
    if (ogrn && ogrn.length > 0) {
        if (query.length > 0) {
            query += ' OR ';
        }
        query += '=@lecm\\-contractor\\:OGRN:\"' + escapeString(ogrn) + '\"';
    }
    query += ')';

    if (args["nodeRef"]) {
        query += " AND NOT ID:\"" + args["nodeRef"] + "\"";
    }

    getDuplicatesInfo(model, query);
};

main();