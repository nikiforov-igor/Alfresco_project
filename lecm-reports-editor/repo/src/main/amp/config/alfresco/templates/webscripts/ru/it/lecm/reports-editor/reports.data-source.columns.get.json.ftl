<#escape x as jsonUtils.encodeJSONString(x)>
[
    <#list columns as column>
    {
    "name": "${column.name}",
    "code": "${column.properties["lecm-rpeditor:dataColumnCode"]!""}",
    "nodeRef": "${column.nodeRef}"
    }
        <#if column_has_next>,</#if>
    </#list>
]
</#escape>