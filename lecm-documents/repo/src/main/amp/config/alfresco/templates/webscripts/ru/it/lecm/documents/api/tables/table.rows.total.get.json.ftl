<#escape x as jsonUtils.encodeJSONString(x)>
[
	<#if items??>
		<#list items as item>
		{
			"itemData": {
				<#list item.properties as prop>
				    "${prop}":  <#if !item.row.properties[prop]??>
				                	""
								<#elseif item.row.properties[prop]?is_date>
									"${xmldate(item.row.properties[prop])}"
								<#else>
									"${item.row.properties[prop]?string}"
								</#if><#if prop_has_next>,</#if>
				</#list>
			},
			"nodeRef": "${item.row.nodeRef}"
		}<#if item_has_next>,</#if>
		</#list>
	</#if>
]
</#escape>