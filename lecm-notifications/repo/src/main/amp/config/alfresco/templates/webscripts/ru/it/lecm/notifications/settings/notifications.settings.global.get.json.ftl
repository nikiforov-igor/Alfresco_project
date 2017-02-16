<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if settingsNode??>
	"nodeRef": "${settingsNode.nodeRef}"
	</#if>
}
</#escape>