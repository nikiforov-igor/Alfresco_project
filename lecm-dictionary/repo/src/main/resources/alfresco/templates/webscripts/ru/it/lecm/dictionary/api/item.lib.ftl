<#macro renderItem item>
	<#escape x as x?string>
	<item name="${item.node.getName()}" nodeRef="${item.node.nodeRef}">
		<#list item.propertiesName as prop>
			<#if !prop?starts_with("sys:")>
				<property name="${prop}">
					<#if item.node.properties[prop]?is_date>
							${item.node.properties[prop]?datetime}
						<#else>
					${item.node.properties[prop]}
					</#if>
				</property>
			</#if>
		</#list>
	</item>
	</#escape>
</#macro>