<#escape x as x?js_string>
[
	<#list  workGroups as wg>
	{
		position: "${wg.assocs["lecm-orgstr:element-member-position-assoc"][0].getNodeRef()}",
		positionName: "${wg.assocs["lecm-orgstr:element-member-position-assoc"][0].getName()}",
		nodeRef: "${wg.getNodeRef().toString()}"
	}
		<#if wg_has_next>,</#if>
	</#list>

]
</#escape>