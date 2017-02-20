<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    <#if folder??>
        "nodeRef": "${folder.nodeRef}"
    </#if>
}
</#escape>