<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#list  branch as b>
	{
		"title": "${b.title}",
		"type": "${b.type}",
		"childType": "${b.childType!""}",
		"nodeRef": "${b.nodeRef}",
		"isLeaf": ${b.isLeaf}
	}
		<#if b_has_next>,</#if>
	</#list>
]
</#escape>