<#escape x as jsonUtils.encodeJSONString(x)>
{
<#if nodeRef??>
	"nodeRef": "${nodeRef}"
<#else/>
	"nodeRef": ""
</#if>
}
</#escape>
