{
    "count": "${count}",
	"tasks": [
		<#list tasks as task>
		{
			"id": "${task.id}",
			"name": "${task.name}",
			"description": "${task.description}",
			"title": "${task.title}",
			"state": "${task.workflowTaskState}",
			"stateMessage": "${task.workflowTaskStateMessage}",
            "dueDate": <#if task.dueDate?exists>
                "${task.dueDate?date}"
            <#else>
                ""
            </#if>,
            "startDate": <#if task.startDate?exists>
                "${task.startDate?date}"
            <#else>
                ""
            </#if>
        }<#if task_has_next>,</#if>
		</#list>
	]
}