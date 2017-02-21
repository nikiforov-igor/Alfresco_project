<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "list": [
        <#list data as report>
        {
            "code" : "${report.reportCode!""}",
            "name" : "${report.reportName!""}",
            "docType" : "${report.documentType!""}",
            "description" : "${report.reportDesc!""}"
        }
            <#if report_has_next>,</#if>
        </#list>
    ]
}
</#escape>