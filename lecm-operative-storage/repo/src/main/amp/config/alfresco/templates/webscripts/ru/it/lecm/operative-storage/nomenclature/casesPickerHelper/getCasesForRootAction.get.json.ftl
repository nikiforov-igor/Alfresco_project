<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	"forArchive": [
		<#list forArchive as item>
			"${item.nodeRef}"<#if item_has_next>,</#if>
		</#list>
	],
	"forDestroy": [
		<#list forDestroy as item>
			"${item.nodeRef}"<#if item_has_next>,</#if>
		</#list>
	]
}
</#escape>
