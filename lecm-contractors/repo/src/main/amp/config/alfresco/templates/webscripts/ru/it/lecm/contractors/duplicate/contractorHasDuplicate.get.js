<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/contractors/duplicate/duplicate.lib.js">

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
        query += '=@lecm\\-contractor\\:INN:\"' + escapeString(inn) + '\"';
    }
    if (kpp && kpp.length > 0) {
        if (query.length > 0) {
            query += ' OR ';
        }
        query += '=@lecm\\-contractor\\:KPP:\"' + escapeString(kpp) + '\"';
    }
    query += ')';

    if (args["nodeRef"]) {
        query += " AND NOT ID:\"" + args["nodeRef"] + "\"";
    }

    getDuplicatesInfo(model, query);
};

main();