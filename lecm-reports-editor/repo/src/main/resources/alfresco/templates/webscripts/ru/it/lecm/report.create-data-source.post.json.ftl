<#escape x as x?js_string>
{
    <#if dataSource??>
    "nodeRef": "${dataSource.nodeRef}"
    </#if>
}
</#escape>
