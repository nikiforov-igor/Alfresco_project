var nodeRef = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id,
	nodeSubstituteString = args['nodeSubstituteString'],
	nodeTitleSubstituteString = args['nodeTitleSubstituteString'],
	selectableType = args['selectableType'],
	useOnlyInSameOrg = args['onlyInSameOrg'],
	statusFilter = url.templateArgs.filterStatus,
	currentOpenCases,
	recursiveOpenCases;

var parentNode = search.findNode(nodeRef);
var branch = [];

// Если условие строгое - то принимаем только те, к которым есть доступ. Если не строгое - проверяем есть ли вообще поле
var useStrictFilterByOrg = (useOnlyInSameOrg != null && ("" + useOnlyInSameOrg) == "true");

if (parentNode != null) {

	var	query = 'PARENT:"' + nodeRef + '" AND {{FILTER_YEARS_BY_ORG({allowAdmin: true})}}';

	switch(statusFilter) {
		case 'ApprovedOnly':
			query = 'PARENT:"' + nodeRef + '" AND {{FILTER_YEARS_BY_ORG({allowAdmin: true})}} AND (@lecm\\-os\\:nomenclature\\-year\\-section\\-status:\"APPROVED\" OR ISNULL:\"lecm-os:nomenclature-year-section-status\" OR NOT EXISTS:\"lecm-os:nomenclature-year-section-status\")';
			break;
		case 'notClosed':
			query = 'PARENT:"' + nodeRef + '" AND {{FILTER_YEARS_BY_ORG({allowAdmin: true})}} AND (ISNULL:\"lecm-os:nomenclature-year-section-status\" OR NOT EXISTS:\"lecm-os:nomenclature-year-section-status\" OR NOT @lecm\\-os\\:nomenclature\\-year\\-section\\-status:\"CLOSED\")';
			break;
	}

	var values = search.query(
		{
			query: searchQueryProcessor.processQuery(query),
			language: "fts-alfresco"
		});

	function getCountOpenCases(qnamePath, isRecursive) {
		var qnamePathResult = isRecursive ? qnamePath + '/' : qnamePath;
		return searchCounter.query({
			language: 'fts-alfresco',
			query: 'PATH:"/' + qnamePathResult + '/*" AND (+TYPE:"lecm-os:nomenclature-case") AND (@lecm\\-os:nomenclature\\-case\\-status:"OPEN")'
		});
	}

	for each(var item in values) {
		if (isSubType(item, selectableType) && (!item.hasAspect("lecm-dic:aspect_active") || item.properties["lecm-dic:active"])
			&& orgstructure.hasAccessToOrgElement(item, useStrictFilterByOrg)) {
			currentOpenCases = getCountOpenCases(item.getQnamePath(), false);
			recursiveOpenCases = getCountOpenCases(item.getQnamePath(), true);
			if (recursiveOpenCases > 0) {
				branch.push({
					label: (nodeSubstituteString != null && nodeSubstituteString.length > 0) ? substitude.formatNodeTitle(item, nodeSubstituteString) : substitude.getObjectDescription(item),
					title: substitude.formatNodeTitle(item, nodeTitleSubstituteString),
					type: item.getTypeShort(),
					nodeRef: item.getNodeRef().toString(),
					isLeaf: "" + (currentOpenCases == recursiveOpenCases),
					isContainer: "" + item.isContainer,
					hasPermAddChildren: lecmPermission.hasPermission(item.nodeRef, "AddChildren")
				});
			}
		}
	}

	function compareLabel(branchA, branchB) {
		if (branchA.label > branchB.label) return 1;
		if (branchA.label < branchB.label) return -1;
		return 0;
	}

	branch.sort(compareLabel);
}

function isSubType(item, typesStr){
    if (typesStr != null && typesStr !== "") {
        var types = typesStr.split(",");
        for (var i = 0; i < types.length; i++) {
            if (types[i].length > 0 && item.isSubType(types[i])) {
                return true;
            }
        }
    }
    return false;
}

model.branch = branch;
