var nodeRef = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id,
	nodeSubstituteString = args['nodeSubstituteString'],
	nodeTitleSubstituteString = args['nodeTitleSubstituteString'],
	selectableType = args['selectableType'],
	useOnlyInSameOrg = args['onlyInSameOrg'],
	statusFilter = url.templateArgs.filterStatus;

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

	function getCountOpenCases(qnamePath) {
		return searchCounter.query({
			language: 'fts-alfresco',
			query: 'PATH:"/' + qnamePath + '//*" AND (+TYPE:"lecm-os:nomenclature-case") AND (@lecm\\-os:nomenclature\\-case\\-status:"OPEN")'
		});
	}

	for each(var item in values) {
		if (isSubType(item, selectableType) && (!item.hasAspect("lecm-dic:aspect_active") || item.properties["lecm-dic:active"])
			&& orgstructure.hasAccessToOrgElement(item, useStrictFilterByOrg)) {
			var isLeaf = !searchCounter.hasChildren(item.getNodeRef().toString(), selectableType, true);

			if (item.getTypeShort() == "lecm-os:nomenclature-unit-section") {
				var unitSections = search.query(
					{
						query: 'PARENT:"' + nodeRef + '" AND (+TYPE:"lecm-os:nomenclature-unit-section")',
						language: "fts-alfresco"
					});
				for each(var unitSection in unitSections) {
					if (getCountOpenCases(unitSection.getQnamePath()) > 0) {
						isLeaf = true;
						break;
					}
				}
			}

			if (getCountOpenCases(item.getQnamePath()) > 0) {
				branch.push({
					label: (nodeSubstituteString != null && nodeSubstituteString.length > 0) ? substitude.formatNodeTitle(item, nodeSubstituteString) : substitude.getObjectDescription(item),
					title: substitude.formatNodeTitle(item, nodeTitleSubstituteString),
					type: item.getTypeShort(),
					nodeRef: item.getNodeRef().toString(),
					isLeaf: "" + isLeaf,
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
