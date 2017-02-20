<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if employee??>
	"firstName": "${employee.properties["lecm-orgstr:employee-first-name"]!''}",
	"middleName": "${employee.properties["lecm-orgstr:employee-middle-name"]!''}",
	"lastName": "${employee.properties["lecm-orgstr:employee-last-name"]!''}",
	"phone": "${employee.properties["lecm-orgstr:employee-phone"]!''}",
	"email": "${employee.properties["lecm-orgstr:employee-email"]!''}",
	"number": "${employee.properties["lecm-orgstr:employee-number"]!''}",
	"fio-g": "${employee.properties["lecm-orgstr:employee-fio-g"]!''}",
	"fio-d": "${employee.properties["lecm-orgstr:employee-fio-d"]!''}",
	"name": "${employee.getName()}",
	"nodeRef": "${employee.getNodeRef().toString()}"
	</#if>
}
</#escape>
