<#escape x as x?js_string>
	{
		"title": "${dictionary.getName()}",
		"type": "${dictionary.getTypeShort()}",
		"nodeRef": "${dictionary.getNodeRef()}",
		"description": "${dictionary.properties["lecm-dic:description"]}",
		"dicType": "${dictionary.properties["lecm-dic:type"]}",
		"attributeForShow": "${dictionary.properties["lecm-dic:attributeForShow"]}"
	}
</#escape>