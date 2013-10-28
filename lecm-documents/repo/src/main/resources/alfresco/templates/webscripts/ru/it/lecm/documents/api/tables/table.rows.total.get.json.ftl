<#escape x as (x!"")?js_string>
[
	<#if items??>
		<#list items as item>
		{
			"itemData": {
				<#list item.properties as prop>
				    "${prop}":  <#if item.row.properties[prop]?is_date>
									"${item.row.properties[prop]?datetime}"
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