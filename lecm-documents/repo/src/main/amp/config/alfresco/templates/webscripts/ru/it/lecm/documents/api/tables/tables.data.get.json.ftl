<#escape x as jsonUtils.encodeJSONString(x)>
	<#if node??>
	{
		"nodeRef": "${node.nodeRef}",
		"rowType": "${node.properties["lecm-document:tableDataRowType"]!}",
		"totalRowType": "${node.properties["lecm-document:tableDataTotalRow"]!}",
		"pageSize": "${node.properties["lecm-document:pageSize"]!}"
	}
	</#if>
</#escape>