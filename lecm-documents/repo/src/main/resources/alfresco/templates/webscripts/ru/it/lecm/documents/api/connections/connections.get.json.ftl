<#escape x as jsonUtils.encodeJSONString(x)>
{
	"hasNext": "${hasNext?string}",
	"items": [
		<#list items as item>
		{
			"nodeRef": "${item.nodeRef}",
			"name": "${item.properties["cm:name"]}",
            "primaryDocument": {
                <#if item.assocs["lecm-connect:primary-document-assoc"]??>
                    "nodeRef": "${item.assocs["lecm-connect:primary-document-assoc"][0].nodeRef}",
                    "name": "${item.assocs["lecm-connect:primary-document-assoc"][0].properties["cm:name"]}",
                    "presentString": "${item.assocs["lecm-connect:primary-document-assoc"][0].properties["lecm-document:present-string"]!''}",
                    "listPresentString": "${item.assocs["lecm-connect:primary-document-assoc"][0].properties["lecm-document:list-present-string"]!''}"
                </#if>
            },
            "connectedDocument": {
                <#if item.assocs["lecm-connect:connected-document-assoc"]??>
                    "nodeRef": "${item.assocs["lecm-connect:connected-document-assoc"][0].nodeRef}",
                    "name": "${item.assocs["lecm-connect:connected-document-assoc"][0].properties["cm:name"]}",
                    "presentString": "${item.assocs["lecm-connect:connected-document-assoc"][0].properties["lecm-document:present-string"]!''}",
                    "listPresentString": "${item.assocs["lecm-connect:connected-document-assoc"][0].properties["lecm-document:list-present-string"]!''}"
                </#if>
            },
            "type": {
                <#if item.assocs["lecm-connect:connection-type-assoc"]??>
                    "nodeRef": "${item.assocs["lecm-connect:connection-type-assoc"][0].nodeRef}",
                    "name": "${item.assocs["lecm-connect:connection-type-assoc"][0].properties["cm:name"]}"
                </#if>
            }
		}<#if item_has_next>,</#if>
		</#list>
	]
}
</#escape>