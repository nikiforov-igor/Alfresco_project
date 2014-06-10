<#escape x as x?js_string>
{
"success": ${success?string}
    <#if templateRef??>
    ,
    "templateRef": "${templateRef}",
    "templateName": "${templateName}"
    </#if>
}
</#escape>