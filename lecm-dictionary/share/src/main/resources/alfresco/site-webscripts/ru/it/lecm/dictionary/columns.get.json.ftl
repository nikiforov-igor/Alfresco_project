<#escape x as x?js_string>
[
	<#list  fields as b>
	{
	fild: "${b.fields}"
	}
		<#if b_has_next>,</#if>
	</#list>
]
</#escape>