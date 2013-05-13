<#escape x as x?js_string>
{
    "showSubordinateTasks": "${data.showSubordinateTasks}",
    "myTasksTotalCount": ${data.myTasksTotalCount},
    "myTasksDisplayedCount": ${data.myTasksDisplayedCount},
	"myTasks": [
		<#list data.myTasks as task>
		{
			"id": "${task.id}",
			"name": "${task.name}",
			"description": "${task.description}",
			"title": "${task.title}",
			"statusMessage": "${task.statusMessage}",
			"type": "${task.type}",
			"typeMessage": "${task.typeMessage}",
			"priority": "${task.priority}",
			"workflowTaskPriority": "${task.workflowTaskPriority}",
			"priorityMessage": "${task.priorityMessage}",
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
	],
	"subordinateTasks": [
		<#list data.subordinateTasks as task>
		{
            "id": "${task.id}",
            "name": "${task.name}",
            "description": "${task.description}",
            "title": "${task.title}",
            "statusMessage": "${task.statusMessage}",
            "type": "${task.type}",
            "typeMessage": "${task.typeMessage}",
            "priority": "${task.priority}",
            "workflowTaskPriority": "${task.workflowTaskPriority}",
            "priorityMessage": "${task.priorityMessage}",
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
</#escape>