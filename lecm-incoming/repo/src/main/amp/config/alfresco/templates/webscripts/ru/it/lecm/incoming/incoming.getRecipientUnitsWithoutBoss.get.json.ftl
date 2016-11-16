<#escape x as jsonUtils.encodeJSONString(x)>
[
    <#if results??>
        <#list results as result>
        {
        "nodeRef": "${result.nodeRef}",
        "name": "${result.name}"
        }<#if result_has_next>,</#if>
        </#list>
    </#if>
]
</#escape>