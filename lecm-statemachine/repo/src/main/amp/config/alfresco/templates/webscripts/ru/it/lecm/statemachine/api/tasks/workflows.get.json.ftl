<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "activeWorkflowsTotalCount": ${data.activeWorkflowsTotalCount},
    "activeWorkflowsDisplayedCount": ${data.activeWorkflowsDisplayedCount},
	"activeWorkflows": [
		<#list data.activeWorkflows as workflow>
		{
			"id": "${workflow.id}",
			"description": "${workflow.description!''}",
			"definition": <#if workflow.definition?exists>
                    "${workflow.definition.title!''}",
                <#else>
                    ""
                </#if>
            "startDate": <#if workflow.startDate?exists>
                    "${workflow.startDate?date}"
                <#else>
                    " - "
                </#if>
        }<#if workflow_has_next>,</#if>
		</#list>
	],
	"completedWorkflows": [
		<#list data.completedWorkflows as workflow>
		{
			"id": "${workflow.id}",
            "description": "${workflow.description!''}",
            "definition": <#if workflow.definition?exists>
                    "${workflow.definition.title!''}",
                <#else>
                    ""
                </#if>
            "startDate": <#if workflow.startDate?exists>
                    "${workflow.startDate?date}"
                <#else>
                    " - "
                </#if>
        }<#if workflow_has_next>,</#if>
		</#list>
	]
}
</#escape>