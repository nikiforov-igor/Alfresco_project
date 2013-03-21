{
    "activeWorkflowsTotalCount": ${data.activeWorkflowsTotalCount},
    "activeWorkflowsDisplayedCount": ${data.activeWorkflowsDisplayedCount},
	"activeWorkflows": [
		<#list data.workflows as workflow>
		{
			"id": "${workflow.id}"
        }<#if workflow_has_next>,</#if>
		</#list>
	],
	"completedWorkflows": [
		<#list data.workflows as workflow>
		{
			"id": "${workflow.id}"
        }<#if workflow_has_next>,</#if>
		</#list>
	]
}