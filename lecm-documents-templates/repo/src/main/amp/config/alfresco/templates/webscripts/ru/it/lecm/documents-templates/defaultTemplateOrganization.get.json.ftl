<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if organization??>
	"nodeRef": "${organization.getNodeRef().toString()}"
	</#if>
}
</#escape>