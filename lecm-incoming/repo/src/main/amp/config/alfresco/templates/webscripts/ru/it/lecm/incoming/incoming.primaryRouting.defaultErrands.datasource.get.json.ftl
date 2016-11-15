<#escape x as jsonUtils.encodeJSONString(x)>
[
    <#if results??>
        <#list results as result>
        {
            "assoc_lecm-errands_executor-assoc": "${result.recipient}",
            "assoc_lecm-errands_type-assoc": "${result.errandType}"
        }<#if result_has_next>,</#if>
        </#list>
    </#if>
]
</#escape>