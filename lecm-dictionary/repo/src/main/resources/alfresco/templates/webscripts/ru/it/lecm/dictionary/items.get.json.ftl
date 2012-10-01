<#escape x as x!""?js_string>
[
	<#list  items as b>
	{
        name: "${b.name}",
        description: "${b.description}"
	}
		<#if b_has_next>,</#if>
	</#list>
]
</#escape>