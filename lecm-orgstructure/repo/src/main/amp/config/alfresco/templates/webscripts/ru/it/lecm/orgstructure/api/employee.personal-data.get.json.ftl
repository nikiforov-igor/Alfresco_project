<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if person??>
	"nodeRef": "${person.getNodeRef()}"
	</#if>
}
</#escape>
