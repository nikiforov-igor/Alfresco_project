<#escape x as jsonUtils.encodeJSONString(x)>
{
	"items": [
	<#list result as item>
		"${item.getNodeRef()}"<#if item_has_next>,</#if>
	</#list>
	]
}
</#escape>
