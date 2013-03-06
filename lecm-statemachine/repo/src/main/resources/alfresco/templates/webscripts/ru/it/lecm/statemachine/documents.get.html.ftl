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
		workflowId: "${state.workflowId!"null"}",
        errors: [
            <#list state.errors as error>
                "${error}"
                <#if error_has_next>,</#if>
            </#list>
            ]
		}
			<#if state_has_next>,</#if>
		</#list>
	],
    workflows: [
        <#list document.workflows as workflow>
        {
        id: "${workflow.id!"null"}",
        label: "${workflow.label}",
        workflowId: "${workflow.workflowId!"null"}",
        assignees: [
                   <#list workflow.assignees as assignee>
                       "${assignee}"
                       <#if assignee_has_next>,</#if>
                   </#list>
        ],
        errors: [
            <#list workflow.errors as error>
            "${error}"
                <#if error_has_next>,</#if>
            </#list>
        ]
        }
            <#if workflow_has_next>,</#if>
        </#list>
    ]
	}
		<#if document_has_next>,</#if>
	</#list>
]
</#escape>