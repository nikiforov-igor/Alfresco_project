<#escape x as x?js_string>
{
	<#if organization??>
	"nodeRef": "${organization.getNodeRef().toString()}"
	</#if>
}
</#escape>