<#escape x as jsonUtils.encodeJSONString(x)!''>
{ "data": [
		<#if results??>
			<#list results as type>
			{
			"name": "${type.value}",
			"value": "${type.key}"
			}<#if type_has_next>,</#if>
			</#list>
		</#if>
	]
}
</#escape>