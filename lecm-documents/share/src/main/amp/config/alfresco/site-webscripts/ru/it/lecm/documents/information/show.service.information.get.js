<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main (){
	AlfrescoUtil.param("nodeRef");
	var url = '/lecm/document/information/getServiceInformation';
	var statemachineVer = "";
	var statemachineId = "";
	var dbId = "";
	var jsonParam = {
			documentRef:  model.nodeRef
		}
	var jsonParamBody = jsonUtils.toJSONString(jsonParam);
	var serviceInfoResponse = remote.connect('alfresco').post(url, jsonParamBody, 'application/json');
	if (serviceInfoResponse.status == 200) {
		var result = eval('(' + serviceInfoResponse + ')');
		statemachineVer = result.statemachineVer;
		statemachineId = result.statemachineId;
		dbId = result.dbId;
	}
	model.statemachineVer = statemachineVer;
	model.statemachineId = statemachineId;
	model.dbId = dbId;
}

main ();