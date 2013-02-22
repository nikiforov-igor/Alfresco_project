<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if defaultConnectionType??>
		"defaultConnectionType": "${defaultConnectionType.nodeRef!""}",
	</#if>
	<#if availableConnectionTypes??>
		"availableConnectionTypes":
		[
			<#list availableConnectionTypes as connectionType>
				{
					"nodeRef": "${connectionType.nodeRef}",
					"name": "${connectionType.properties.name}"
				}
				<#if connectionType_has_next>,</#if>
			</#list>
		]
	</#if>
}
</#escape>