<#escape x as jsonUtils.encodeJSONString(x)>
{
<#if settings??>
	"nodeRef": "${settings.nodeRef}"
<#else>
	"nodeRef": null
</#if>
}
</#escape>
