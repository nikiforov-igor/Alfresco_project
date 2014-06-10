<#escape x as x?js_string>
{
	<#if position??>
	"primaryPosition":"${position.getNodeRef()}"
	</#if>
}
</#escape>