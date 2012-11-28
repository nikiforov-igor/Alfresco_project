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
                    ]
                }
                    <#if action_has_next>,</#if>
                </#list>
            ],
            userActions: [
                <#list status.userActions as action>
                {
                    actionName: "${action.actionName!"null"}",
                    actionId: "${action.actionId!"null"}",
                    nodeRef: "${action.nodeRef!"null"}",
                    transitions: [
                    ]
                }
                    <#if action_has_next>,</#if>
                </#list>
            ],
            transitionActions: [
                <#list status.transitionActions as action>
                {
                actionName: "${action.actionName!"null"}",
                actionId: "${action.actionId!"null"}",
                nodeRef: "${action.nodeRef!"null"}",
                transitions: [
                    "trans1",
                    "trans2",
                    "trans3"
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
