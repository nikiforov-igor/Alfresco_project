<#escape x as x?js_string>
[
	<#list statuses as status>
	{
		name: "${status.name}",
		label: "${status.label}"
	}
		<#if status_has_next>,</#if>
	</#list>
]
</#escape>