<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if boss??>
	"bossExists": "${boss.getNodeRef()}"
	</#if>
}
</#escape>