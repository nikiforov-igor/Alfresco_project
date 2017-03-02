<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	"title": "${dictionary.getName()}",
	"type": "${dictionary.getTypeShort()}",
	"nodeRef": "${dictionary.getNodeRef()}",
	"description": "${dictionary.properties["lecm-dic:description"]?j_string}",
	"itemType": "${dictionary.properties["lecm-dic:type"]?string}",
	"attributeForShow": "${dictionary.properties["lecm-dic:attributeForShow"]?string}",
	"plane": "${dictionary.properties["lecm-dic:plane"]?string}"
}
</#escape>
