<#escape x as x?js_string>
[
	<#list  workGroups as wg>
	{
		groupName: "${wg.getParent().getName()}",
		groupRef: "${wg.getParent().getNodeRef().toString()}",
        role: "${wg.assocs["lecm-orgstr:element-member-position-assoc"][0].getNodeRef()}",
        roleName: "${wg.assocs["lecm-orgstr:element-member-position-assoc"][0].getName()}",
		nodeRef: "${wg.getNodeRef().toString()}"
	}
		<#if wg_has_next>,</#if>
	</#list>

]
</#escape>