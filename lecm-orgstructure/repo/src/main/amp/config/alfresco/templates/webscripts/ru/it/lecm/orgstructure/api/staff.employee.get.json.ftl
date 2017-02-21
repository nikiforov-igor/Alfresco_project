<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if link??>
		"is_primary": "${link.properties["lecm-orgstr:employee-link-is-primary"]?string}",
		"employee": "${link.assocs["lecm-orgstr:employee-link-employee-assoc"][0].getNodeRef().toString()}",
		"nodeRef": "${link.getNodeRef().toString()}"
	</#if>
}
</#escape>