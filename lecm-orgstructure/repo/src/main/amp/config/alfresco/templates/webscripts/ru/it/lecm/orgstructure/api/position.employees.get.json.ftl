<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#list employees as employee>
	{
		"firstName": "${employee.properties["lecm-orgstr:employee-first-name"]}",
		"middleName": "${employee.properties["lecm-orgstr:employee-middle-name"]!''}",
		"lastName": "${employee.properties["lecm-orgstr:employee-last-name"]}",
        "shortName": "${employee.properties["lecm-orgstr:employee-short-name"]!''}",
		"phone": "${employee.properties["lecm-orgstr:employee-phone"]!''}",
		"email": "${employee.properties["lecm-orgstr:employee-email"]!''}",
		"number": "${employee.properties["lecm-orgstr:employee-number"]!''}",
		"fioG": "${employee.properties["lecm-orgstr:employee-fio-g"]!''}",
		"fioD": "${employee.properties["lecm-orgstr:employee-fio-d"]!''}",
		"name": "${employee.getName()}",
		"nodeRef": "${employee.getNodeRef()}"
	}
		<#if employee_has_next>,</#if>
	</#list>
]
</#escape>