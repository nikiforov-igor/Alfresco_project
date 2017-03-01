<#escape x as jsonUtils.encodeJSONString(x)!''>
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
            "documentPresentString": "${task.documentPresentString}",
            "statusMessage": "${task.statusMessage}",
			"type": "${task.type}",
			"typeMessage": "${task.typeMessage}",
			"priority": "${task.priority}",
			"workflowTaskPriority": "${task.workflowTaskPriority}",
			"priorityMessage": "${task.priorityMessage}",
            "dueDate": <#if task.dueDate?exists>
                "${task.dueDate?date?string}"
            <#else>
                ""
            </#if>,
            "startDate": <#if task.startDate?exists>
                "${task.startDate?date?string}"
            <#else>
                ""
            </#if>
        }<#if task_has_next>,</#if>
		</#list>
	],
    <#if data.myLatestTask?exists>
        "myLatestTask" : {
            "id": "${data.myLatestTask.id}",
            "startDate": <#if data.myLatestTask.startDate?exists>
                "${data.myLatestTask.startDate?date?string}"
            <#else>
                ""
            </#if>
        },
    </#if>
	"subordinateTasks": [
		<#list data.subordinateTasks as task>
		{
            "id": "${task.id}",
            "name": "${task.name}",
            "description": "${task.description}",
            "title": "${task.title}",
            "documentPresentString": "${task.documentPresentString}",
            "statusMessage": "${task.statusMessage}",
            "type": "${task.type}",
            "typeMessage": "${task.typeMessage}",
            "priority": "${task.priority}",
            "workflowTaskPriority": "${task.workflowTaskPriority}",
            "priorityMessage": "${task.priorityMessage}",
            "dueDate": <#if task.dueDate?exists>
                "${task.dueDate?date?string}"
            <#else>
                ""
            </#if>,
            "startDate": <#if task.startDate?exists>
                "${task.startDate?date?string}"
            <#else>
                ""
            </#if>
        }<#if task_has_next>,</#if>
		</#list>
	]
}
</#escape>
