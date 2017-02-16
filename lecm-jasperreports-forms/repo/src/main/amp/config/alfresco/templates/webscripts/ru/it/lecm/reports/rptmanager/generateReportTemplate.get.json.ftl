<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "success": ${success?string}
    <#if templateRef??>
    ,
    "templateRef": "${templateRef}",
    "templateName": "${templateName}"
    </#if>
}
</#escape>