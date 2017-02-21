<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    <#if result??>
    "nodeRef": "${result}"
    </#if>
}
</#escape>
