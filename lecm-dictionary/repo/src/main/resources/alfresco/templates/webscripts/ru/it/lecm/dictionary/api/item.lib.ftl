<#macro renderItem item>
	<item name="${item.node.getName()}" nodeRef="${item.node.nodeRef}" type="${item.node.typeShort}">
		<@renderProperties item=item/>
	</item>
</#macro>

<#macro renderProperties item>
	<#list item.propertiesName as prop>
		<#if !prop?starts_with("sys:")>
			<property name="${prop}">
				<#if item.node.properties[prop]?is_date>
						${item.node.properties[prop]?datetime}
					<#else>
				${item.node.properties[prop]?string}
				</#if>
			</property>
		</#if>
	</#list>
</#macro>