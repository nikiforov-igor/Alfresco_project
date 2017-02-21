<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if nodeRef??>
	"nodeRef": "${nodeRef}"
	</#if>
}
</#escape>