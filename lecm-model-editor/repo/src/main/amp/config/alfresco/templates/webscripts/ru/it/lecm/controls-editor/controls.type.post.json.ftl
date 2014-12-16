<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if typeRoot??>
	"typeRoot": "${typeRoot.nodeRef}"
	<#else>
	"typeRoot": null
	</#if>
}
</#escape>
