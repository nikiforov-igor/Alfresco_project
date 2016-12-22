<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#if items?? && (items?size > 0)>
		<#list items as item>
		{
			"nodeRef": "${item.nodeRef}",
			"name":"${item.name!""}",
			"limitationDate": "<#if item.limitationDate??>${item.limitationDate?string("dd MMM yyyy")}</#if>",
			"executorNodeRef":"${item.executorNodeRef!""}",
			"executorName":"${item.executorName!""}",
			"autoClose": ${item.autoClose?string}
		}<#if item_has_next>,</#if>
		</#list>
	</#if>
]
</#escape>