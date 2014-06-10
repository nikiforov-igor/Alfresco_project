<#escape x as x?js_string>
{
	<#if photo??>
		nodeRef:"${photo.getNodeRef()}"
	</#if>
}
</#escape>