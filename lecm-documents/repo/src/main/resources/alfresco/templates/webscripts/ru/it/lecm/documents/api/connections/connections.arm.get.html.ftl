<#assign trCount = 0/>

<table class="arm-connections-table">
	<tbody>
	   <#if connections??>
	       <#list connections as item>
	            <#if item.properties["lecm-connect:is-system"]>
		            <#assign document = item.assocs["lecm-connect:connected-document-assoc"][0]/>
		            <#if document??>
			            <#assign trCount = trCount + 1/>
		                <tr>
				            <td class="connection-type-column">${item.assocs["lecm-connect:connection-type-assoc"][0].properties["cm:name"]}</td>
				            <td class="connection-present-column">
				                <a href="/share/page/document?nodeRef=${document.nodeRef}">
				                    ${document.properties["lecm-document:ext-present-string"]!''}
				                </a>
					            <p>${document.properties["lecm-document:list-present-string"]!''}</p>
				            </td>
			            </tr>
		            </#if>
	            </#if>
	       </#list>
	   </#if>

	   <#if connectionsWithDocument??>
	       <#list connectionsWithDocument as item>
	            <#if item.properties["lecm-connect:is-system"]>
		            <#assign document = item.assocs["lecm-connect:primary-document-assoc"][0]/>
		            <#if document??>
			            <#assign trCount = trCount + 1/>
		                <tr>
				            <td class="connection-type-column">${item.assocs["lecm-connect:connection-type-assoc"][0].properties["cm:name"]}</td>
				            <td class="connection-present-column">
				                <a href="/share/page/document?nodeRef=${document.nodeRef}">
				                    ${document.properties["lecm-document:ext-present-string"]!''}
				                </a>
					            <p>${document.properties["lecm-document:list-present-string"]!''}</p>
				            </td>
			            </tr>
		            </#if>
	            </#if>
	       </#list>
	   </#if>

		<#if trCount == 0>
			<tr class="no-records">
				<td>Нет связанных документов</td>
			</tr>
		</#if>
	</tbody>
</table>
