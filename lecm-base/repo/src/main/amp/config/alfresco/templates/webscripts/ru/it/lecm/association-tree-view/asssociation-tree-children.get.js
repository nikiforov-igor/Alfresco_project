var nodeRef = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;
var nodeSubstituteString = args['nodeSubstituteString'];
var nodeTitleSubstituteString = args['nodeTitleSubstituteString'];
var selectableType = args['selectableType'];
var useOnlyInSameOrg = args['onlyInSameOrg'];
var sortProp = args['sortProp'];

var parentNode = search.findNode(nodeRef);
var branch = [];

// Если условие строгое - то принимаем только те, к которым есть доступ. Если не строгое - проверяем есть ли вообще поле
var useStrictFilterByOrg = (useOnlyInSameOrg != null && ("" + useOnlyInSameOrg) == "true");

if (parentNode != null) {
    var values = parentNode.getChildren();

    for each(var item in values) {
		if (isSubType(item, selectableType) && (!item.hasAspect("lecm-dic:aspect_active") || item.properties["lecm-dic:active"])
            && orgstructure.hasAccessToOrgElement(item, useStrictFilterByOrg)) {

			var label = (nodeSubstituteString != null && nodeSubstituteString.length > 0) ? substitude.formatNodeTitle(item, nodeSubstituteString) : substitude.getObjectDescription(item);
			var sortPropValue = label;
			if (sortProp != null) {
				sortPropValue = item.properties[sortProp];
			}

	        branch.push({
	            label: label,
	            title: substitude.formatNodeTitle(item, nodeTitleSubstituteString),
	            type: item.getTypeShort(),
	            nodeRef: item.getNodeRef().toString(),
	            isLeaf: "" + !searchCounter.hasChildren(item.getNodeRef().toString(), selectableType, true),
	            isContainer: "" + item.isContainer,
		        hasPermAddChildren: lecmPermission.hasPermission(item.nodeRef, "AddChildren"),
		        sortProp: sortPropValue
	        });
		}
    }

	branch.sort(sortBranch);
}

function sortBranch(item1, item2) {
	var val1 = item1.sortProp.toUpperCase(),
		val2 = item2.sortProp.toUpperCase();
	return (val1 > val2) ? 1 : (val1 < val2) ? -1 : 0;
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