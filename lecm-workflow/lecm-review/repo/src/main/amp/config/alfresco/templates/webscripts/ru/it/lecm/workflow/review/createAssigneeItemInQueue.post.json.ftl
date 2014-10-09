<#escape x as jsonUtils.encodeJSONString(x)>
[
	<#list assigneeItems as assigneeItem>
		{
			"nodeRef": "${assigneeItem.nodeRef.toString()}"
		}<#if assigneeItem_has_next>,</#if>
	</#list>
]
</#escape>
