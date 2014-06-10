<#escape x as x?js_string>
{ "data": [
    <#list result as item>
    {
    "name": "${item.label}",
    "value": "${item.value}"
    }
        <#if item_has_next>,</#if>
    </#list>
]}
</#escape>