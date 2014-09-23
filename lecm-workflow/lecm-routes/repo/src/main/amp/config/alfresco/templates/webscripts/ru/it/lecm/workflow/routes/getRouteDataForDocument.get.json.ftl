<#escape x as jsonUtils.encodeJSONString(x)>
{
	"routeType": "${routeType}",
	"stageType": "${stageType}",
	"stageItemType": "${stageItemType}",
	"currentIterationNode": "${currentIterationNode}",
	"approvalState": "${approvalState}",
	"approvalResult": {
		"result":"${approvalResult}",
		"title": "${approvalResultTitle}"
	},
	"completedApprovalsCount": ${completedApprovalsCount},
	"sourceRouteInfo": "${sourceRouteInfo}",
	"approvalIsEditable": ${approvalIsEditable?string},
	"approvalHistoryFolder": "${approvalHistoryFolder}"
}
</#escape>
