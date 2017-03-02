<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#list  staffs as staff>
	{
		"position": "${staff.assocs["lecm-orgstr:element-member-position-assoc"][0].getNodeRef()}",
		"positionName": "${staff.assocs["lecm-orgstr:element-member-position-assoc"][0].getName()}",
	<#if staff.assocs["lecm-orgstr:element-member-employee-assoc"]??>
        "employee": "${staff.assocs["lecm-orgstr:element-member-employee-assoc"][0].assocs["lecm-orgstr:employee-link-employee-assoc"][0].getNodeRef()}",
        "employeeName": "${staff.assocs["lecm-orgstr:element-member-employee-assoc"][0].assocs["lecm-orgstr:employee-link-employee-assoc"][0].getName()}",
	</#if>
		"nodeRef": "${staff.getNodeRef().toString()}"
	}
		<#if staff_has_next>,</#if>
	</#list>
]
</#escape>