<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if createdAssoc??>"createdAssoc": "${createdAssoc?string}",</#if>
	"message": "${message}"
}
</#escape>
