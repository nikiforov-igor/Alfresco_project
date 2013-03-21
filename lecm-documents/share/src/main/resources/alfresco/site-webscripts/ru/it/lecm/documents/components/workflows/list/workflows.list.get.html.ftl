<#assign id = args.htmlid?js_string>

<#if data??>
<div class="workflow-tasks-container">
    <div class="body scrollableList" id="${id}_results">

        <div class="workflow-tasks-category">
            <div class="workflow-tasks-category-title">${msg("tasklist.label.workflows.active")}</div>

            <#--<#list data.myTasks as task>-->
                <#--<div class="workflow-task">-->
                    <#--<div class="workflow-task-list-picture ${task.workflowTaskPriority}" title="${task.priorityMessage}">&nbsp;</div>-->
                    <#--<div style="float: left;">-->
                        <#--<div>-->
                            <#--<div class="workflow-task-title workflow-task-list-left-column" style="font-size: 16px;">-->
                                <#--<a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>-->
                            <#--</div>-->
                            <#--<span class="workflow-task-status ${task.type}">${task.typeMessage}</span>-->
                        <#--</div>-->
                        <#--<div style="clear: both;"></div>-->
                        <#--<div class="workflow-description">${task.description}</div>-->
                        <#--<div>-->
                            <#--<div class="workflow-task-list-left-column">-->
                                <#--<span class="workflow-task-list-label">${msg("tasklist.label.duedate")}:&nbsp;</span>${task.dueDate}-->
                            <#--</div>-->
                            <#--<span class="workflow-task-list-label">${msg("tasklist.label.status")}: </span>${task.statusMessage}-->
                        <#--</div>-->
                    <#--</div>-->
                    <#--<div style="clear: both;"></div>-->
                <#--</div>-->
            <#--</#list>-->
        </div>

        <div class="workflow-tasks-category">
            <div class="workflow-tasks-category-title">${msg("tasklist.label.workflows.completed")}</div>

            <#--<#list data.subordinateTasks as task>-->
                <#--<div class="workflow-task">-->
                    <#--<div class="workflow-task-list-picture ${task.workflowTaskPriority}" title="${task.priorityMessage}">&nbsp;</div>-->
                    <#--<div style="float: left;">-->
                        <#--<div>-->
                            <#--<div class="workflow-task-title workflow-task-list-left-column" style="font-size: 16px;">-->
                                <#--<a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>-->
                            <#--</div>-->
                            <#--<span class="workflow-task-status ${task.type}">${task.typeMessage}</span>-->
                        <#--</div>-->
                        <#--<div style="clear: both;"></div>-->
                        <#--<div class="workflow-description">${task.description}</div>-->
                        <#--<div>-->
                            <#--<div class="workflow-task-list-left-column">-->
                                <#--<span class="workflow-task-list-label">${msg("tasklist.label.duedate")}:&nbsp;</span>${task.dueDate}-->
                            <#--</div>-->
                            <#--<span class="workflow-task-list-label">${msg("tasklist.label.status")}: </span>${task.statusMessage}-->
                        <#--</div>-->
                    <#--</div>-->
                    <#--<div style="clear: both;"></div>-->
                <#--</div>-->
            <#--</#list>-->
        </div>

    </div>
</div>
</#if>