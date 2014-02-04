<#escape x as jsonUtils.encodeJSONString(x)>
[
	<#list types as type>
		{
			"name": "${type.name}",
			"title": "${type.title}"
		}<#if type_has_next>,</#if>
	</#list>
]
</#escape>