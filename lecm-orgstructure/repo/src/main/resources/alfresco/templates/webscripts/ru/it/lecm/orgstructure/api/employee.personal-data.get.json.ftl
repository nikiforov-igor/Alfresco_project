<#escape x as x?js_string>
{
	<#if person??>
	nodeRef:"${person.getNodeRef()}"
	</#if>
}
</#escape>