<#escape x as x?js_string>
{ "data": [
		<#if results??>
			<#list results?keys as type>
			{
			"name": "${results[type]}",
			"value": "${type}"
			}<#if type_has_next>,</#if>
			</#list>
		</#if>
	]
}
</#escape>