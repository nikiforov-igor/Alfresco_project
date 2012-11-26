<#escape x as x?js_string>
[
	<#list  staffs as staff>
	{
		position: "${staff.assocs["lecm-orgstr:element-member-position-assoc"][0].getNodeRef()}",
		positionName: "${staff.assocs["lecm-orgstr:element-member-position-assoc"][0].getName()}",
		nodeRef: "${staff.getNodeRef().toString()}"
	}
		<#if staff_has_next>,</#if>
	</#list>

]
</#escape>