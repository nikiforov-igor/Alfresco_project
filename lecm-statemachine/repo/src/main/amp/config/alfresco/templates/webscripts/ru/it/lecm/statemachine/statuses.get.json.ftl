<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#list statuses as status>
    {
        "id": "${status}"
    }<#if status_has_next>,</#if>
    </#list>
]
</#escape>