<#escape x as x?js_string>
[
	"count": ${positionsCount},
	"positions":
	[
		<#list  positions as pos>
		{
			name: "${pos.getName()}",
			nodeRef: "${pos.getNodeRef().toString()}"
		}
			<#if pos_has_next>,</#if>
		</#list>
	]
]
</#escape>