<#escape x as jsonUtils.encodeJSONString(x)>
[
    <#if results??>
        <#list results as result>
        {
            <#if result.executionDateDays??>
                "prop_lecm-errands_limitation-date-days": "${result.executionDateDays}",
            </#if>
            <#if result.executionDateType??>
                "prop_lecm-errands_limitation-date-type": "${result.executionDateType}",
            </#if>
            "readonly_assoc_lecm-errands_executor-assoc": "${result.recipient}",
            "assoc_lecm-errands_type-assoc": "${result.errandType}"
        }<#if result_has_next>,</#if>
        </#list>
    </#if>
]
</#escape>