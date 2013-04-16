<#escape x as x?js_string>
[
    <#if filters??>
        <#assign keys = filters?keys>
        <#list keys as key>
        {
            "name" :"${key}",
            "statuses" : "${filters[key]}"
        }<#if key_has_next>,</#if>
        </#list>
    </#if>
]
</#escape>