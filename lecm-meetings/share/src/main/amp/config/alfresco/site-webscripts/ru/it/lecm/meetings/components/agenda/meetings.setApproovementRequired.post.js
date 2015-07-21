<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
	//AlfrescoUtil.param("nodeRef");
	//AlfrescoUtil.param("value");
	
//	var clientRequest = json.toString();
//	var clientJSON = eval('(' + clientRequest + ')');
	
	var url = '/lecm/meetings/setApproovementRequired';
	var body = {
		nodeRef: args["nodeRef"],
		value: args["value"]
	};
	model.answer="SUCCESS";
	var result = remote.connect("alfresco").post(url, jsonUtils.toJSONString(body), "application/json");
	if (result.status != 200) {
		model.answer="FAIL!!!";
	}
}


main();
