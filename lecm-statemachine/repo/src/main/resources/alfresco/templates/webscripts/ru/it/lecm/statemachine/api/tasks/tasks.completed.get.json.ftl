{
    "count": "${count}",
	"tasks": [
		<#list tasks as task>
		{
			"id": "${task.id}",
			"name": "${task.name}",
            "description": "${task.description}",
            "title": "${task.title}"
        }<#if task_has_next>,</#if>
		</#list>
	]
}