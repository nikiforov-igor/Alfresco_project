<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if photo??>
		"nodeRef": "${photo.getNodeRef()}"
	</#if>
}
</#escape>
