<#escape x as x?js_string>
{
	firstName: "${employee.properties["lecm-orgstr:employee-first-name"]}",
	middleName: "${employee.properties["lecm-orgstr:employee-middle-name"]}",
	lastName: "${employee.properties["lecm-orgstr:employee-last-name"]}",
	sex: "${employee.properties["lecm-orgstr:employee-sex"]}",
	phone: "${employee.properties["lecm-orgstr:employee-phone"]!''}",
	email: "${employee.properties["lecm-orgstr:employee-email"]!''}",
	number: "${employee.properties["lecm-orgstr:employee-number"]!''}",
	fio-g: "${employee.properties["lecm-orgstr:employee-fio-g"]!''}",
	fio-d: "${employee.properties["lecm-orgstr:employee-fio-d"]!''}",
	system-user: "${employee.properties["lecm-orgstr:employee-is-system-user"]?string}",
	name: "${employee.getName()}",
	active: "${employee.properties["lecm-dic:active"]?string}",
	nodeRef:"${employee.getNodeRef()}"
}
</#escape>