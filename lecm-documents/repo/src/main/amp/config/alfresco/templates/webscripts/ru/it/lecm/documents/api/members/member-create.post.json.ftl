<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if member??>
        "nodeRef": "${member.nodeRef}",
		"name": "${member.getName()}",
		"employeeRef": "${member.assocs["lecm-doc-members:employee-assoc"][0].nodeRef}"
	</#if>
}
</#escape>