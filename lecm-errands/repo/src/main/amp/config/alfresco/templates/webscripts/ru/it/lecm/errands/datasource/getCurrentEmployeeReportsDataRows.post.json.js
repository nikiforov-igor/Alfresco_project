<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">

    function main() {
        var DEFAULT_PAGE_SIZE = 20;
        var allReports = [];
        var filteredReports = [];
        var params = {};
        if (typeof json !== "undefined" && json.has("params")) {
            var pars = json.get("params");
            params = {
                searchConfig: (pars.get('searchConfig').length() > 0) ? eval("(" + pars.get('searchConfig') + ")") : null,
                sort: (pars.get('sort').length() > 0)  ? pars.get('sort') : null,
                maxResults:(pars.get('maxResults') !== null) ? parseInt(pars.get('maxResults'), 10) : DEFAULT_PAGE_SIZE,
                fields:(pars.get('fields').length() > 0) ? pars.get('fields') : null,
                nameSubstituteStrings:(pars.get('nameSubstituteStrings') !== null) ? pars.get('nameSubstituteStrings') : null,
                parent: (pars.get('parent').length() > 0)  ? pars.get('parent') : null,
                searchNodes: (pars.get('searchNodes').length() > 0)  ? pars.get('searchNodes').split(',') : null,
                showInactive: pars.has('showInactive') ? ('' + pars.get('showInactive') == 'true') : false,
                itemType:(pars.get('itemType').length() > 0)  ? pars.get('itemType') : null,
                startIndex: pars.has('startIndex') ? parseInt(pars.get('startIndex'), 10) : DEFAULT_INDEX,
                useChildQuery: pars.has('useChildQuery') ? ('' + pars.get('useChildQuery') == 'true') : false,
                useFilterByOrg: pars.has('useFilterByOrg') ? ('' + pars.get('useFilterByOrg') == 'true') : true,
                useOnlyInSameOrg: pars.has('useOnlyInSameOrg') ? ('' + pars.get('useOnlyInSameOrg') == 'true') : false,
                filter: pars.has('filter')  ? pars.get('filter') : null
            };
            var showDecline = '' + params.searchConfig.showDecline == 'true';
            var currentUser = orgstructure.getCurrentEmployee();
            allReports = documentTables.getTableDataRows(params.parent);
            filteredReports = allReports.filter(function (report) {
                var reportCoexecutor = report.assocs["lecm-errands-ts:coexecutor-assoc"][0];
                var reportStatus = report.properties["lecm-errands-ts:coexecutor-report-status"];
                if (reportStatus == "PROJECT" && !currentUser.nodeRef.equals(reportCoexecutor.nodeRef)) {
                    return false;
                }
                if(!showDecline){
                    if (reportStatus == "DECLINE") {
                        return false;
                    }
                }
                return true;
            });
            var results = [];
            for(var i = params.startIndex; i < params.startIndex + params.maxResults; i++){
                if(filteredReports[i]) {
                    results.push(filteredReports[i]);
                }
            }

        }
        model.data = processResults(results, params.fields, params.nameSubstituteStrings, params.startIndex, filteredReports.length);

    };

main();
