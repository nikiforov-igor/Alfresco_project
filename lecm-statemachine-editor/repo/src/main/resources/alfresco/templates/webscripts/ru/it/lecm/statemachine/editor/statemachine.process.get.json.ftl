<#escape x as x?js_string>
{
    packageNodeRef: "${packageNodeRef}",
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
    startActions: [
        <#list status.startActions as action>
            <@printAction action />
            <#if action_has_next>,</#if>
        </#list>
    ],
    userActions: [
        <#list status.userActions as action>
            <@printAction action />
            <#if action_has_next>,</#if>
        </#list>
    ],
    transitionActions: [
        <#list status.transitionActions as action>
            <@printAction action />
            <#if action_has_next>,</#if>
        </#list>
    ],
    endActions: [
        <#list status.endActions as action>
            <@printAction action />
            <#if action_has_next>,</#if>
        </#list>
    ]
}
</#macro>

<#macro printAction action>
{
    actionName: "${action.actionName!"null"}",
    actionId: "${action.actionId!"null"}",
    nodeRef: "${action.nodeRef!"null"}",
    transitions: [
                <#list action.transitions as transition>
                    "${transition}"
                    <#if transition_has_next>,</#if>
                </#list>
                 ]
}
</#macro>
</#escape>
