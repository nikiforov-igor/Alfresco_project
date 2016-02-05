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
	"completedCurrentApprovalsCount": ${completedCurrentApprovalsCount},
	"completedHistoryApprovalsCount": ${completedHistoryApprovalsCount},
	"sourceRouteInfo": "${sourceRouteInfo}",
	"approvalIsEditable": ${approvalIsEditable?string},
	"approvalHistoryFolder": "${approvalHistoryFolder}"
}
</#escape>
