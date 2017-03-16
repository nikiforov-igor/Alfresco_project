<#escape x as x?js_string>
{ "data": [
		<#if result??>
			<#list result?keys as type>
			{
			"name": "${result[type]}",
			"value": "${type}"
			}<#if type_has_next>,</#if>
			</#list>
		</#if>
	]
}
</#escape>