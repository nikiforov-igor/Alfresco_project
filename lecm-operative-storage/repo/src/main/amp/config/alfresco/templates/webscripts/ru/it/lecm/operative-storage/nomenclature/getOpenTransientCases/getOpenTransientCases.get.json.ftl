<#escape x as jsonUtils.encodeJSONString(x)>
{
	"items": [
	<#list items as item>
		"${item}"<#if item_has_next>,</#if>
	</#list>
	]
}
</#escape>
