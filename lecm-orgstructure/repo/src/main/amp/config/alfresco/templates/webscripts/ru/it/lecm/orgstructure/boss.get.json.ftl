<#escape x as x?js_string>
{
	<#if boss??>
	bossExists:"${boss.getNodeRef()}"
	</#if>
}
</#escape>