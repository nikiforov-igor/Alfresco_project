<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if attribute??>
		"nodeRef": "${attribute.nodeRef}",
		"targetType": "${targetType!''}"
	</#if>
}
</#escape>