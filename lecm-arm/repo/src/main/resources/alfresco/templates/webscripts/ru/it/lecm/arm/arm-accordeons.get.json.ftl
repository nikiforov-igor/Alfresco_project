<#escape x as x?js_string>
[
<#if values??>
    <#list values as value>
    {
    "nodeRef": "${value.nodeRef}",
    "name": "${value.name}"
    }
        <#if value_has_next>,</#if>
    </#list>
</#if>
]
</#escape>