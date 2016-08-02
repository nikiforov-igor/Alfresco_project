<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if defaultConnectionType??>
		"defaultConnectionType": <#if !defaultConnectionType.properties['lecm-connect-types:for-auto-create']?? || !defaultConnectionType.properties['lecm-connect-types:for-auto-create']>"${defaultConnectionType.nodeRef!""}"<#else>""</#if>,
	</#if>
	<#if recommendedConnectionTypes??>
		"recommendedConnectionTypes":
		[
			<#list recommendedConnectionTypes as connectionType>
				{
					"nodeRef": "${connectionType.nodeRef}",
					"name": "${connectionType.properties.name}",
					"isAuto": <#if connectionType.properties['lecm-connect-types:for-auto-create']??>${connectionType.properties['lecm-connect-types:for-auto-create']?string}<#else>false</#if>
				}
				<#if connectionType_has_next>,</#if>
			</#list>
		],
	</#if>
	<#if availableConnectionTypes??>
		"availableConnectionTypes":
		[
			<#list availableConnectionTypes as connectionType>
				{
					"nodeRef": "${connectionType.nodeRef}",
					"name": "${connectionType.properties.name}",
            		"isAuto": <#if connectionType.properties['lecm-connect-types:for-auto-create']??>${connectionType.properties['lecm-connect-types:for-auto-create']?string}<#else>false</#if>
				}
				<#if connectionType_has_next>,</#if>
			</#list>
		],
	</#if>
    <#if existConnectionTypes??>
        "existConnectionTypes":
        [
            <#list existConnectionTypes as connectionType>
                {
                    "nodeRef": "${connectionType.nodeRef}",
                    "name": "${connectionType.properties.name}",
            		"isAuto": <#if connectionType.properties['lecm-connect-types:for-auto-create']??>${connectionType.properties['lecm-connect-types:for-auto-create']?string}<#else>false</#if>
                }
                <#if connectionType_has_next>,</#if>
            </#list>
        ]
    </#if>
}
</#escape>