<#escape x as x?js_string>
[
    <#list statuses as status>
    {
        "id" :"${status}"
    }<#if status_has_next>,</#if>
    </#list>
]
</#escape>