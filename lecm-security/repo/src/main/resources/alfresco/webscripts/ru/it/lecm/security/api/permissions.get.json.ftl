<#escape x as x?js_string>
[
	<#if permissions??>
		<#list permissions as item>
			${item?string}<#if item_has_next>,</#if>
		</#list>
	</#if>
]
</#escape>