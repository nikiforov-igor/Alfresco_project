<#import "connections.lib.ftl" as connectionsLib />

<#escape x as jsonUtils.encodeJSONString(x)>
{
	"items": [
		<#list items as item>
			<#if item??>{
				"previosDocRef" : "${documentRef}",
				<#if item.assocs["lecm-connect:primary-document-assoc"]?? && item.assocs["lecm-connect:primary-document-assoc"][0].nodeRef!=documentRef>
					"title":"${item.assocs["lecm-connect:primary-document-assoc"][0].properties["lecm-document:ext-present-string"]!''}",
					"nodeRef":"${item.assocs["lecm-connect:primary-document-assoc"][0].nodeRef}",
					"docType":"${item.assocs["lecm-connect:primary-document-assoc"][0].typeShort}",
					"status":"${item.assocs["lecm-connect:primary-document-assoc"][0].properties["lecm-statemachine:status"]!''}",
					<#if item.assocs["lecm-connect:connection-type-assoc"]??>
						"connectionType": "${item.assocs["lecm-connect:connection-type-assoc"][0].properties["lecm-connect-types:reverse-name"]!''}",
					</#if>
					"direction":"reverse"
				<#elseif item.assocs["lecm-connect:connected-document-assoc"]??>
					"title":"${item.assocs["lecm-connect:connected-document-assoc"][0].properties["lecm-document:ext-present-string"]!''}",
					"nodeRef":"${item.assocs["lecm-connect:connected-document-assoc"][0].nodeRef}",
					"docType":"${item.assocs["lecm-connect:connected-document-assoc"][0].typeShort}",
					"status":"${item.assocs["lecm-connect:connected-document-assoc"][0].properties["lecm-statemachine:status"]!''}",
					<#if item.assocs["lecm-connect:connection-type-assoc"]??>
						"connectionType": "${item.assocs["lecm-connect:connection-type-assoc"][0].properties["cm:name"]}",
					</#if>
					"direction":"direct"
				</#if>
			}<#if item_has_next>,</#if>
			</#if>
		</#list>
	]
}
</#escape>
