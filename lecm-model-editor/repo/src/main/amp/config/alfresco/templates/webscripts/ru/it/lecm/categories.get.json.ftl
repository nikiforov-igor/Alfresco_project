<#escape x as x?js_string>
{ "data": [
		<#if result??>
			<#list result as type>
			{
			"name": "${type}"
			}<#if type_has_next>,</#if>
			</#list>
		</#if>
	]
}
</#escape>