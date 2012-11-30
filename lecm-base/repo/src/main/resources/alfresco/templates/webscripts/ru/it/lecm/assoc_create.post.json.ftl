<#escape x as x?js_string>
{
	<#if createdAssoc??>"createdAssoc": "${createdAssoc?string}",</#if>
	"message": "${message}"
}
</#escape>
