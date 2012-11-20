<#escape x as x?js_string>
{
start: [
        <#list startActions as action>
        {
            id: "${action.id}",
            title: "${action.title}"
        }
            <#if action_has_next>,</#if>
        </#list>
        ],
take: [
    <#list takeActions as action>
    {
    id: "${action.id}",
    title: "${action.title}"
    }
        <#if action_has_next>,</#if>
    </#list>
],
end: [
    <#list endActions as action>
    {
    id: "${action.id}",
    title: "${action.title}"
    }
        <#if action_has_next>,</#if>
    </#list>
],
}
</#escape>