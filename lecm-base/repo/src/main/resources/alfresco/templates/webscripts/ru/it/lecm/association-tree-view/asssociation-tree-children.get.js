<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/substitude.lib.js">

var nodeRef = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;
var nodeSubstituteString = args['nodeSubstituteString'];
var nodeTitleSubstituteString = args['nodeTitleSubstituteString'];
var selectableType = args['selectableType'];

var parentNode = search.findNode(nodeRef);
var branch = [];

if (parentNode != null) {
    var values = parentNode.getChildren();

    for each(var item in values) {
		if (item.isSubType(selectableType) && (!item.hasAspect("lecm-dic:aspect_active") || item.properties["lecm-dic:active"])) {
	        branch.push({
	            label: formatNodeTitle(item, nodeSubstituteString),
	            title: formatNodeTitle(item, nodeTitleSubstituteString),
	            type: item.getTypeShort(),
	            nodeRef: item.getNodeRef().toString(),
	            isLeaf: "" + !item.hasChildren,
	            isContainer: "" + item.isContainer
	        });
		}
    }
}

model.branch = branch;