<#escape x as x?js_string>
{
	<#if boss??>
		firstName: "${boss.properties["lecm-orgstr:employee-first-name"]!''}",
		middleName: "${boss.properties["lecm-orgstr:employee-middle-name"]!''}",
		lastName: "${boss.properties["lecm-orgstr:employee-last-name"]!''}",
		name: "${boss.getName()}",
		active: "${boss.properties["lecm-dic:active"]?string}",
		nodeRef:"${boss.getNodeRef()}"
	</#if>
}
</#escape>