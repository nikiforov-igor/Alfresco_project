<#escape x as jsonUtils.encodeJSONString(x)>
[
	<#if rows??>
		<#list rows as row>
		{
			"nodeRef": "${row.nodeRef}",
			"name": "${row.name}"
		}<#if row_has_next>,</#if>
		</#list>
	</#if>
]
</#escape>