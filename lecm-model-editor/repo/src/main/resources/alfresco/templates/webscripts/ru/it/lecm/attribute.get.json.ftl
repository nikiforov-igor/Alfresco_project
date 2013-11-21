<#escape x as x?js_string>
{
	<#if attribute??>
		"nodeRef": "${attribute.nodeRef}",
		"targetType": "${targetType!''}"
	</#if>
}
</#escape>