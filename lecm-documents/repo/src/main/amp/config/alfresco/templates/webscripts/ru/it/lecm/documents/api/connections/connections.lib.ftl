<#macro renderConnections items>
	<#escape x as jsonUtils.encodeJSONString(x)>
		[
			<#list items as item>
				<#assign primaryDoc = item.assocs["lecm-connect:primary-document-assoc"][0]>
				<#assign connectedDoc = item.assocs["lecm-connect:connected-document-assoc"][0]>
				{
					"nodeRef": "${item.nodeRef}",
					"name": "${item.properties["cm:name"]}",
					"isSystem": ${item.properties["lecm-connect:is-system"]?string},
					"primaryDocument": {
						<#if item.assocs["lecm-connect:primary-document-assoc"]??>
                        	"hasAccess": ${lecmPermission.hasReadAccess(primaryDoc.nodeRef)?string},
							"nodeRef": "${primaryDoc.nodeRef}",
	                        "viewUrl": "${documentService.getViewUrl(primaryDoc.nodeRef)}",
    	                    "type": "${primaryDoc.typeShort}",
							"name": "${primaryDoc.properties["cm:name"]}",
							"presentString": <#if isMlSupported && primaryDoc.properties["lecm-document:ml-present-string"]?has_content>"${primaryDoc.properties["lecm-document:ml-present-string"]}"<#else>"${primaryDoc.properties["lecm-document:present-string"]!''}"</#if>,
							"extPresentString": <#if isMlSupported && primaryDoc.properties["lecm-document:ml-ext-present-string"]?has_content>"${primaryDoc.properties["lecm-document:ml-ext-present-string"]}"<#else>"${primaryDoc.properties["lecm-document:ext-present-string"]!''}"</#if>,
							"listPresentString": <#if isMlSupported && primaryDoc.properties["lecm-document:ml-list-present-string"]?has_content>"${primaryDoc.properties["lecm-document:ml-list-present-string"]}"<#else>"${primaryDoc.properties["lecm-document:list-present-string"]!''}"</#if>
						</#if>
					},
					"connectedDocument": {
						<#if item.assocs["lecm-connect:connected-document-assoc"]??>
                        	"hasAccess": ${lecmPermission.hasReadAccess(connectedDoc.nodeRef)?string},
							"nodeRef": "${connectedDoc.nodeRef}",
	                        "viewUrl": "${documentService.getViewUrl(connectedDoc.nodeRef)}",
							"type": "${connectedDoc.typeShort}",
							"name": "${connectedDoc.properties["cm:name"]}",
							"presentString": <#if isMlSupported && connectedDoc.properties["lecm-document:ml-present-string"]?has_content>"${connectedDoc.properties["lecm-document:ml-present-string"]}"<#else>"${connectedDoc.properties["lecm-document:present-string"]!''}"</#if>,
							"extPresentString": <#if isMlSupported && connectedDoc.properties["lecm-document:ml-ext-present-string"]?has_content>"${connectedDoc.properties["lecm-document:ml-ext-present-string"]}"<#else>"${connectedDoc.properties["lecm-document:ext-present-string"]!''}"</#if>,
							"listPresentString": <#if isMlSupported && connectedDoc.properties["lecm-document:ml-list-present-string"]?has_content>"${connectedDoc.properties["lecm-document:ml-list-present-string"]}"<#else>"${connectedDoc.properties["lecm-document:list-present-string"]!''}"</#if>
						</#if>
					},
					"type": {
						<#if item.assocs["lecm-connect:connection-type-assoc"]??>
							"nodeRef": "${item.assocs["lecm-connect:connection-type-assoc"][0].nodeRef}",
							"name": "${item.assocs["lecm-connect:connection-type-assoc"][0].properties["cm:name"]}",
							"reverseName": "${item.assocs["lecm-connect:connection-type-assoc"][0].properties["lecm-connect-types:reverse-name"]!''}"
						</#if>
					}
				}<#if item_has_next>,</#if>
			</#list>
		]
	</#escape>
</#macro>
