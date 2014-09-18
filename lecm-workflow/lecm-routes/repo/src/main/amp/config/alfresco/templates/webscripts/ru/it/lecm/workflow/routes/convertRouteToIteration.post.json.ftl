<#escape x as jsonUtils.encodeJSONString(x)>
{
	"nodeRef": "${iterationNode}",
	"stageItems": [
		<#list stageItems as stageItem>
			"${stageItem.nodeRef.toString()}"<#if stageItem_has_next>,</#if>
		</#list>
	],
	"scriptErrors": [
		<#list scriptErrors as scriptError>
			"${scriptError}"<#if scriptError_has_next>,</#if>
		</#list>
	]
}
</#escape>
