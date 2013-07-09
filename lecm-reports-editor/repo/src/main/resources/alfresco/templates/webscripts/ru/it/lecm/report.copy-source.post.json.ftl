<#escape x as x?js_string>
{
    <#if result??>
    "nodeRef": "${result.nodeRef?string}",
    "success": "${result.success?string}"
    </#if>

}
</#escape>