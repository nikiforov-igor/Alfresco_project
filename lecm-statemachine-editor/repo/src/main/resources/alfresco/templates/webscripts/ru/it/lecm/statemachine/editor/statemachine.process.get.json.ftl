<#escape x as x?js_string>
{
    packageNodeRef: "${packageNodeRef}",
    statuses: [
    <#list statuses as status>
        {
            name: "${status.name}",
            nodeRef: "${status.nodeRef}",
            startActions: [
                <#list status.startActions as action>
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
            ],
            takeActions: [
                <#list status.takeActions as action>
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
            ],
            endActions: [
                <#list status.endActions as action>
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
