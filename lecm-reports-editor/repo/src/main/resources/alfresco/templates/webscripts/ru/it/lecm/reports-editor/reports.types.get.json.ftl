<#escape x as x?js_string>
[
    <#list types as type>
    {
        "name": "${type.name}",
        "nodeRef": "${type.nodeRef}"
    }
        <#if type_has_next>,</#if>
    </#list>
]
</#escape>