<#escape x as x?js_string>
{
"success": ${success?string}
    <#if reportRef??>
    ,
    "reportRef": "${reportRef}",
    "reportName": "${reportName}"
    </#if>
}
</#escape>