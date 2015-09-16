<#escape x as x!""?js_string>
{
"items" : [
<#if results??>
    <#list results as reportInfo>
        {
            "name": "${reportInfo.reportName}",
            "value": "${reportInfo.reportCode}"
        }<#if reportInfo_has_next>,</#if>
    </#list>
</#if>
]
}
</#escape>