<#escape x as x?js_string>
{
	<#if mainjob??>
	mainJobExists:"${mainjob.getNodeRef()}"
	</#if>
}
</#escape>