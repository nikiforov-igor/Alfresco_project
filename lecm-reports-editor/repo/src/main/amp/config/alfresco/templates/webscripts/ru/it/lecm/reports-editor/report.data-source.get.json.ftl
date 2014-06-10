<#escape x as x?js_string>
{
    <#if dataSource??>
    "name": "${dataSource.name!""}",
    "code": "${dataSource.properties["lecm-rpeditor:dataSourceCode"]!""}",
    "nodeRef": "${dataSource.nodeRef}"
    </#if>
}
</#escape>
