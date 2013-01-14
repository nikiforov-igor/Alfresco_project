<#escape x as x?js_string>
{
    packageNodeRef: "${packageNodeRef}",
    machineNodeRef: "${machineNodeRef}",
    statuses: [
    <#list statuses as status>
        <@printStatus status />
        <#if status_has_next>,</#if>
    </#list>
    ]
}

<#macro printStatus status>
{
    name: "${status.name}",
    nodeRef: "${status.nodeRef}",
    editable: ${status.editable},
    transitions: [
        <#list status.transitions as transition>
            <@printTransition transition />
            <#if transition_has_next>,</#if>
        </#list>
    ]
}
</#macro>

<#macro printTransition transition>
    {
        user: ${transition.user?string},
        exp: "${transition.exp!"null"}",
        status: "${transition.status}"
    }
</#macro>

</#escape>
