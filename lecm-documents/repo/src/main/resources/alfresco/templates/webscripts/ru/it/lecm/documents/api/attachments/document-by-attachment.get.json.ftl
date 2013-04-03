<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if document??>
		"nodeRef": "${document.nodeRef}",
		"name": "${document.properties["cm:name"]}",
		"presentString": "${document.properties["lecm-document:present-string"]!''}",
		"listPresentString": "${document.properties["lecm-document:list-present-string"]!''}"
	</#if>
}
</#escape>