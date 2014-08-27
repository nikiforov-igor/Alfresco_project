<#escape x as jsonUtils.encodeJSONString(x)>
[
	<#list stageItems as stageItem>
		{
			"nodeRef": "${stageItem.nodeRef.toString()}"
		}<#if stageItem_has_next>,</#if>
	</#list>
]
</#escape>
