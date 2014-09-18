var nodeRef = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;
var nodeSubstituteString = args['nodeSubstituteString'];
var nodeTitleSubstituteString = args['nodeTitleSubstituteString'];
var selectableType = args['selectableType'];
var useOnlyInSameOrg = args['onlyInSameOrg'];

var parentNode = search.findNode(nodeRef);
var branch = [];

// Если условие строгое - то принимаем только те, к которым есть доступ. Если не строгое - проверяем есть ли вообще поле
var useStrictFilterByOrg = (useOnlyInSameOrg != null && ("" + useOnlyInSameOrg) == "true");

if (parentNode != null) {
    var values = parentNode.getChildren();

    for each(var item in values) {
		if (item.isSubType(selectableType) && (!item.hasAspect("lecm-dic:aspect_active") || item.properties["lecm-dic:active"])
            && orgstructure.hasAccessToOrgElement(item, useStrictFilterByOrg)) {
	        branch.push({
	            label: substitude.formatNodeTitle(item, nodeSubstituteString),
	            title: substitude.formatNodeTitle(item, nodeTitleSubstituteString),
	            type: item.getTypeShort(),
	            nodeRef: item.getNodeRef().toString(),
	            isLeaf: "" + !searchCounter.hasChildren(item.getNodeRef().toString(), selectableType, true),
	            isContainer: "" + item.isContainer,
		        hasPermAddChildren: lecmPermission.hasPermission(item.nodeRef, "AddChildren")
	        });
		}
    }
}

model.branch = branch;