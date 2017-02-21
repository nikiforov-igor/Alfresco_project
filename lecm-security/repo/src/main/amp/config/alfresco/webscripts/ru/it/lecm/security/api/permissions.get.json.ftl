<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#if permissions??>
		<#list permissions as item>
			${item?string}<#if item_has_next>,</#if>
		</#list>
	</#if>
]
</#escape>