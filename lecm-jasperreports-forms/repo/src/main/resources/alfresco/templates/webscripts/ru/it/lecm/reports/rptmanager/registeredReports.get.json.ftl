<#escape x as x?js_string>
{
"list": [
    <#list data as report>
    {
    "code" : "${report.reportCode!""}",
    "name" : "${report.reportName!""}"
    }
        <#if report_has_next>,</#if>
    </#list>
]
}
</#escape>