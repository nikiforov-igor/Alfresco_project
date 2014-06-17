<#escape x as x?js_string>
[
	<#list  branch as b>
	{
		label: "${b.label}",
		title: "${b.title}",
		type: "${b.type}",
		nodeRef: "${b.nodeRef}",
		isLeaf: ${b.isLeaf},
        isContainer: ${b.isContainer},
		hasPermAddChildren: ${b.hasPermAddChildren?string}
	}
		<#if b_has_next>,</#if>
	</#list>
]
</#escape>