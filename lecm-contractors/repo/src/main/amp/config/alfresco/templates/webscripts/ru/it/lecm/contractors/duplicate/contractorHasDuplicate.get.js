<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/contractors/duplicate/duplicate.lib.js">

function main() {
    var fullName = args["fullName"] ? args["fullName"] : null;
    var shortName = args["shortName"] ? args["shortName"] : null;
    var inn = args["inn"] ? args["inn"] : null;
    var kpp = args["kpp"] ? args["kpp"] : null;

    var query = 'TYPE:\"lecm-contractor:contractor-type\" AND NOT @lecm\\-dic\\:active:false';

    if (args["nodeRef"]) {
        query += " AND NOT ID:\"" + args["nodeRef"] + "\"";
    }

    var paramsQuery = concatQuery('', '', '=@lecm\\-contractor\\:fullname\\-search', contractorsRootObject.formatContractorName(fullName ? fullName : ''), true);
    paramsQuery = concatQuery(paramsQuery, 'OR', '=@lecm\\-contractor\\:shortname\\-search', contractorsRootObject.formatContractorName(shortName ? shortName : ''), true);
    paramsQuery = concatQuery(paramsQuery, 'OR', '=@lecm\\-contractor\\:INN', inn);
    paramsQuery = concatQuery(paramsQuery, 'OR', '=@lecm\\-contractor\\:KPP', kpp);


    if (paramsQuery) {
        query += (' AND (' + paramsQuery + ')');
    }

    if (logger.isLoggingEnabled()) {
        logger.log("Query for search contractor duplicates:\r\n" + query);
    }
    getDuplicatesInfo(model, query);
};

main();