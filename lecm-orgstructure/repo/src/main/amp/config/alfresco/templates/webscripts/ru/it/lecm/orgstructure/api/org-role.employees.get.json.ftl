<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#list employees as employee>
	{
		"nodeRef": "${employee.getNodeRef().toString()}",
		"shortName": "${employee.properties["lecm-orgstr:employee-short-name"]!""}"
	}
		<#if employee_has_next>,</#if>
	</#list>
]
</#escape>