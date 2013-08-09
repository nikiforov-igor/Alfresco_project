<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#if coexecs?? && coexecs?size gt 0>
        <#list coexecs as item>
        {
            "employeeRef": "${item.nodeRef}",
            "employeeName":"${item.employeeName!""}",
            "employeePosition":"${item.employeePosition!""}"
        }<#if item_has_next>,</#if>
        </#list>
    </#if>
]
</#escape>