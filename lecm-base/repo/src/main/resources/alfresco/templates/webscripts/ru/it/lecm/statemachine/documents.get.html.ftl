<#escape x as x?js_string>
[
    <#list documents as document>
        {
            nodeRef: "${document.nodeRef}",
            name: "${document.name}",
            status: "${document.status}",
            taskId: "${document.taskId}",
            states: [
                <#list document.states as state>
                    {
                        actionId: "${state.actionId}",
                        label: "${state.label}",
                        workflowId: "${state.workflowId}"
                    }
                    <#if state_has_next>,</#if>
                </#list>
            ]
        }
        <#if document_has_next>,</#if>
    </#list>
]
</#escape>