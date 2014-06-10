<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if category??>
		"nodeRef": "${category.node.nodeRef}",
		"name": "${category.node.properties["cm:name"]}",
		"isReadOnly": ${category.isReadOnly?string}
	</#if>
}
</#escape>