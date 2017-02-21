<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    <#if result??>
    "nodeRef": "${result.nodeRef?string}",
    "success": "${result.success?string}"
    </#if>

}
</#escape>