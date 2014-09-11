<#escape x as jsonUtils.encodeJSONString(x)>
{
	"routeType": "${routeType}",
	"stageType": "${stageType}",
	"stageItemType": "${stageItemType}",
	"currentIterationNode": "${currentIterationNode}",
	"approvalState": "${approvalState}",
	"completedApprovalsCount": ${completedApprovalsCount},
	"sourceRouteInfo": "${sourceRouteInfo}",
	"approvalIsEditable": ${approvalIsEditable?string},
	"approvalHistoryFolder": "${approvalHistoryFolder}"
}
</#escape>
