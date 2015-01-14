<#escape x as jsonUtils.encodeJSONString(x)>
{
	"nodeRef": <#if nodeRef??>"${nodeRef}"<#else>null</#if>,
	"nodeType": <#if nodeType??>"${nodeType}"<#else>null</#if>,
	"fileName": <#if fileName??>"${fileName}"<#else>""</#if>
}
</#escape>
