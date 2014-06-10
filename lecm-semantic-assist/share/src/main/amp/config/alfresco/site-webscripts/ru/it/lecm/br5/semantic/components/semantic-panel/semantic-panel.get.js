<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
function main(){

	var nodeRef = AlfrescoUtil.param('nodeRef');
	if (nodeRef) {
		model.nodeRef = nodeRef;
		var url = '/lecm/br5/semantic/aspect/has-br5-aspect?sDocument=' + nodeRef;
		var result = remote.connect("alfresco").get(url);

		if (result.status == 200) {
			model.aspect = eval('(' + result + ')');
		}
	}

}

main();