<#escape x as jsonUtils.encodeJSONString(x)>
{
"nodes": [
	<#if result?? >
		<#list result as resultItem>
			"${resultItem.nodeRef.toString()}"
			<#if resultItem_has_next>,</#if>
		</#list>
	</#if>
]
}
</#escape>