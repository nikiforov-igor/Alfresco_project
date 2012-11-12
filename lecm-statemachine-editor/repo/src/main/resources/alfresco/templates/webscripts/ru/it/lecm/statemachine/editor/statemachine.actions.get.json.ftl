<#escape x as x?js_string>
[
    <#list actions as action>
    {
        id: "${action.id}",
        title: "${action.title}"
    }
        <#if action_has_next>,</#if>
    </#list>
]
</#escape>