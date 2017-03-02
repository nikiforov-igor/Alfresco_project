<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "hasNext": <#if next?? && (next?size > 0)>"true"<#else>"false"</#if>,
    "items": [
        <#list members as item>
        {
        "nodeRef": "${item.nodeRef}",
        "group": "${item.group!""}",
        "employeeName":"${item.employeeName!""}",
        "employeePosition":"${item.employeePosition!""}",
        "employeeRef": "${item.employeeRef}"
        }<#if item_has_next>,</#if>
        </#list>
    ]
}
</#escape>