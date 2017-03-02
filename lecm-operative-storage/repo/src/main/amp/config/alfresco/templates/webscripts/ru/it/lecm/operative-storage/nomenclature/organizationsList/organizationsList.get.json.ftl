<#escape x as jsonUtils.encodeJSONString(x)!''>
{ "data": [
	<#if results??>
		<#list results as item>
		{
			"name": "${item.name}",
			"value": "${item.value}"
		}<#if item_has_next>,</#if>
		</#list>
	</#if>
	]
}
</#escape>