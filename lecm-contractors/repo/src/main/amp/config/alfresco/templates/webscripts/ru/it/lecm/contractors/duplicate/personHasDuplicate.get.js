<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/contractors/duplicate/duplicate.lib.js">

function main() {
    var lastName = args["lastName"] ? args["lastName"] : null;
    var firstName = args["firstName"] ? args["firstName"] : null;
    var middleName = args["middleName"] ? args["middleName"] : null;
    var region = args["region"] ? args["region"] : null;
    var inn = args["inn"] ? args["inn"] : null;
    var ogrn = args["ogrn"] ? args["ogrn"] : null;

    var query = 'TYPE:\"lecm-contractor:physical-person-type\" AND NOT @lecm\\-dic\\:active:false';

    if (args["nodeRef"]) {
        query += " AND NOT ID:\"" + args["nodeRef"] + "\"";
    }

    var subQueryByNames = concatQuery('', '', '=@lecm\\-contractor\\:lastName', lastName);
    subQueryByNames = concatQuery(subQueryByNames, 'AND', '=@lecm\\-contractor\\:firstName', firstName);
    subQueryByNames = concatQuery(subQueryByNames, 'AND', '=@lecm\\-contractor\\:region\\-association\\-ref', region, true);

    var middleNameQuery = concatQuery('', '', '=@lecm\\-contractor\\:middleName', middleName);
    if (middleNameQuery) {
        middleNameQuery = concatQuery(middleNameQuery, 'OR', 'NOT @lecm\\-contractor\\:middleName', '?*', true);
    }

    if (middleNameQuery) {
        subQueryByNames += (' AND (' + middleNameQuery + ')')
    }

    var paramsQuery = subQueryByNames;
    if (paramsQuery) {
        paramsQuery = '(' + paramsQuery + ')'
    }

    paramsQuery = concatQuery(paramsQuery, 'OR', '=@lecm\\-contractor\\:INN', inn);
    paramsQuery = concatQuery(paramsQuery, 'OR', '=@lecm\\-contractor\\:OGRN', ogrn);

    if (paramsQuery) {
        query += (' AND (' + paramsQuery + ')');
    }

    if (logger.isLoggingEnabled()) {
        logger.log("Query for search person duplicates:\r\n" + query);
    }

    getDuplicatesInfo(model, query);
};

main();