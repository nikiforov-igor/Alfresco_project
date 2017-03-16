<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    <#if dataSource??>
    "nodeRef": "${dataSource.nodeRef}"
    </#if>
}
</#escape>
