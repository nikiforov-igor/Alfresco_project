<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    <#if dataSource??>
    "name": "${dataSource.name!""}",
    "code": "${dataSource.properties["lecm-rpeditor:dataSourceCode"]!""}",
    "nodeRef": "${dataSource.nodeRef}"
    </#if>
}
</#escape>
