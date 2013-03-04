<#escape x as jsonUtils.encodeJSONString(x)>
{
	"hasNext": <#if next?? && (next?size > 0)>"true"<#else>"false"</#if>,
	"items": [
		<#list connections as item>
		{
			"nodeRef": "${item.nodeRef}",
			"name": "${item.properties["cm:name"]}",
            "primaryDocument": {
                <#if item.assocs["lecm-connect:primary-document-assoc"]??>
                    "nodeRef": "${item.assocs["lecm-connect:primary-document-assoc"][0].nodeRef}",
                    "name": "${item.assocs["lecm-connect:primary-document-assoc"][0].properties["cm:name"]}"
                </#if>
            },
            "connectedDocument": {
                <#if item.assocs["lecm-connect:connected-document-assoc"]??>
                    "nodeRef": "${item.assocs["lecm-connect:connected-document-assoc"][0].nodeRef}",
                    "name": "${item.assocs["lecm-connect:connected-document-assoc"][0].properties["cm:name"]}"
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