<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if category??>
		"nodeRef": "${category.node.nodeRef}",
		"name": <#if category.node.properties["cm:title"]?has_content>"${category.node.properties["cm:title"]}"<#else>"${category.node.name}"</#if>,
		"isReadOnly": ${category.isReadOnly?string}
	</#if>
}
</#escape>
