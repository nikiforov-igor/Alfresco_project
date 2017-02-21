<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "success": ${success?string}
    <#if reportRef??>
    ,
    "reportRef": "${reportRef}",
    "reportName": "${reportName}"
    </#if>
}
</#escape>