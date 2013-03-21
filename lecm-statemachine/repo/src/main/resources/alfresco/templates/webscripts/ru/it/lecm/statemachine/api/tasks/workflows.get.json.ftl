{
    "activeWorkflowsTotalCount": ${data.activeWorkflowsTotalCount},
    "activeWorkflowsDisplayedCount": ${data.activeWorkflowsDisplayedCount},
	"activeWorkflows": [
		<#list data.activeWorkflows as workflow>
		{
			"id": "${workflow.id}"
        }<#if workflow_has_next>,</#if>
		</#list>
	],
	"completedWorkflows": [
		<#list data.completedWorkflows as workflow>
		{
			"id": "${workflow.id}"
        }<#if workflow_has_next>,</#if>
		</#list>
	]
}