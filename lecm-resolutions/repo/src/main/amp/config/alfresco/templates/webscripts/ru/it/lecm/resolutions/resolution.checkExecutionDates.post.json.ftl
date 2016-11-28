<#escape x as jsonUtils.encodeJSONString(x)>
{
"total": ${total?string},
"errands": [
    <#if results??>
        <#list results as result>
        {
        "result": ${result?string},
        }<#if result_has_next>,</#if>
        </#list>
    </#if>
]
}
</#escape>