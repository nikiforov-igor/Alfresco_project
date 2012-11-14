<#escape x as x?js_string>
{
    packageNodeRef: "${packageNodeRef}",
    statuses: [
    <#list statuses as status>
        {
            name: "${status.name}",
            nodeRef: "${status.nodeRef}",
            actions: [
                <#list status.actions as action>
                {
                    actionName: "${action.actionName!"null"}",
                    actionId: "${action.actionId!"null"}",
                    nodeRef: "${action.nodeRef!"null"}",
                    transitions: [
                        "Trans1",
                        "Trans2",
                        "Trans3"
                    ]
                }
                    <#if action_has_next>,</#if>
                </#list>
            ]
        }
        <#if status_has_next>,</#if>
    </#list>
    ]
}
</#escape>
