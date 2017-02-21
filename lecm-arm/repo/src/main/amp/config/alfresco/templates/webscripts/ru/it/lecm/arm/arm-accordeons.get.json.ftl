<#escape x as jsonUtils.encodeJSONString(x)!''>
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
