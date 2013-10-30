<#escape x as jsonUtils.encodeJSONString(x)>
	<#if node??>
	{
		"nodeRef": "${node.nodeRef}",
		"rowType": "${node.properties["lecm-document:tableDataRowType"]!}",
		"totalRowType": "${node.properties["lecm-document:tableDataTotalRow"]!}",
		"rows": [
			<#if rows??>
				<#list rows as row>
					"${row.nodeRef}"<#if row_has_next>,</#if>
				</#list>
			</#if>
		],
		"totalRows": [
			<#if totalRows??>
				<#list totalRows as totalRow>
					"${totalRow.nodeRef}"<#if totalRow_has_next>,</#if>
				</#list>
			</#if>
		]
	}
	</#if>
</#escape>