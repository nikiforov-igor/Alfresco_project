<#escape x as x?js_string>
{
	<#if settingsNode??>
	"nodeRef": "${settingsNode.nodeRef}"
	</#if>
}
</#escape>