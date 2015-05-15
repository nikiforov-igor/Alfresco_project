<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if items?exists>
	"items": [
		<#list items as item>
		{
			"nodeRef": "${item.nodeRef}"
		}<#if item_has_next>,</#if>
		</#list>
	]
	</#if>
}
</#escape>
