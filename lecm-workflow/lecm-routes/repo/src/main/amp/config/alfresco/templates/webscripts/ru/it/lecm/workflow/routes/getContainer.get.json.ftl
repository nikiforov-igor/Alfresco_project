<#escape x as jsonUtils.encodeJSONString(x)>
{
	"nodeRef": "${routesContainer}",
	"routeType": "${routeType}",
	"stageType": "${stageType}",
	"stageItemType": "${stageItemType}",
	"isEngineer": ${isEngineer?string}
}
</#escape>
