<#escape x as x?js_string>
[
    <#list machines as machine>
    {
    id: "${machine.id}",
    title: "${machine.title!""}",
    description: "${machine.description!""}"
    }
        <#if machine_has_next>,</#if>
    </#list>
]
</#escape>