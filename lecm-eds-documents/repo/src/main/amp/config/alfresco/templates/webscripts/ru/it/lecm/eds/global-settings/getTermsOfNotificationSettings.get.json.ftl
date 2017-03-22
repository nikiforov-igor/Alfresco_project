<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    <#if node??>
    "nodeRef": "${node.nodeRef}"
    </#if>
}
</#escape>