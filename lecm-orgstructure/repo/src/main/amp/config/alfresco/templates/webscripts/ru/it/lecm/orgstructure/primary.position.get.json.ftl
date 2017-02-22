<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if position??>
	"primaryPosition": "${position.getNodeRef()}"
	</#if>
}
</#escape>