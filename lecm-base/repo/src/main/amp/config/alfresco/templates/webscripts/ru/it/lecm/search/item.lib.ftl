<#macro itemJSON item>
    <#escape x as jsonUtils.encodeJSONString(x)>
    <#assign node = item.node>
    "nodeRef": "${node.nodeRef}",
    "type": "${node.typeShort}",
    "page": "${item.page!"document"}",
    "createdOn": "${xmldate(node.properties.created)}",
    "createdBy":
    {
    "value": "${item.createdBy.userName}",
    "displayValue": "${item.createdBy.displayName}"
    },
    "modifiedOn": "${xmldate(node.properties.modified)}",
    "modifiedBy":
    {
    "value": "${item.modifiedBy.userName}",
    "displayValue": "${item.modifiedBy.displayName}"
    },
    "permissions":
    {
    "userAccess":
    {
        <#list item.actionPermissions?keys as actionPerm>
            <#if item.actionPermissions[actionPerm]?is_boolean>
            "${actionPerm?string}": ${item.actionPermissions[actionPerm]?string}<#if actionPerm_has_next>,</#if>
            </#if>
        </#list>
    }
    },
    "itemData":
    {
        <#list item.nodeData?keys as key>
            <#assign itemData = item.nodeData[key]>
        "${key}":
            <#if itemData?is_sequence>
            [
                <#list itemData as data>
                    <@renderData data /><#if data_has_next>,</#if>
                </#list>
            ]
            <#else>
                <@renderData itemData />
            </#if><#if key_has_next>,</#if>
        </#list>
    }
    </#escape>
</#macro>

<#macro renderData data>
    <#escape x as jsonUtils.encodeJSONString(x)>
    {
        <#if data.value?is_boolean>
        "value": ${data.value?string},
        <#elseif data.value?is_number>
        "value": ${data.value?c},
		<#elseif data.value?is_enumerable>
			<#assign propValue = "">
			<#list data.value as value>
				<#assign propValue = propValue + value>
				<#if value_has_next>
					<#assign propValue = propValue + ",">
				</#if>
			</#list>
		"value": "${propValue}",
        <#else>
        "value": "${data.value}",
        </#if>
        <#if data.metadata??>
        "metadata": "${data.metadata}",
        </#if>
        <#if data.displayValue?is_boolean>
        "displayValue": ${data.displayValue?string}
        <#elseif data.displayValue?is_number>
        "displayValue": ${data.displayValue?c}
		<#elseif data.displayValue?is_enumerable>
			<#assign propDisplayValue = "">
			<#list data.displayValue as displayValue>
				<#assign propDisplayValue = propDisplayValue + displayValue>
				<#if displayValue_has_next>
					<#assign propDisplayValue = propDisplayValue + ",">
				</#if>
			</#list>
		"displayValue": "${propDisplayValue}"
        <#else>
        "displayValue": "${data.displayValue}"
        </#if>
    }
    </#escape>
</#macro>