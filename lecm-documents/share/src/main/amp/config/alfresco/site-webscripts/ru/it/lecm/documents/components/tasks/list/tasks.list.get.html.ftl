<#assign id = args.htmlid?js_string>

<#if data??>

<#assign activeSeleted = "">
<#if tasksState == "active">
    <#assign activeSeleted = "selected">
</#if>

<#assign completedSeleted = "">
<#if tasksState == "completed">
    <#assign completedSeleted = "selected">
</#if>

<#assign allSeleted = "">
<#if tasksState == "all" || (completedSeleted == "" && activeSeleted == "")>
    <#assign allSeleted = "selected">
</#if>

    <div class="list-category">
        <div class="list-category-title">${msg("tasklist.label.mytasks")}</div>
        <div class="tasks-list-filter">
            <select id="${id}-tasks-states">
                <option ${allSeleted} value="all">${msg("tasklist.option.all")}</option>
                <option ${activeSeleted} value="active">${msg("tasklist.option.active")}</option>
                <option ${completedSeleted} value="completed">${msg("tasklist.option.completed")}</option>
            </select>
        </div>
        <#if data.myTasks?size == 0>
            <div class="workflow-task-line">
                ${msg("tasklist.label.no-tasks")}
            </div>
        </#if>
        <#list data.myTasks as task>
            <div class="workflow-task-item">
                <div class="workflow-task-list-picture ${task.workflowTaskPriority}" title="${task.priorityMessage}">&nbsp;</div>
                <div class="left1">
                    <div>
                        <div class="workflow-task-title workflow-task-list-left-column">
                            <a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>
                        </div>
                        <span class="workflow-task-status ${task.type}">${task.typeMessage}</span>
                    </div>
                    <div class="clear"></div>
                    <div class="workflow-task-description">${task.description}</div>
                    <div>
                        <div class="workflow-task-list-left-column">
                            <#if task.dueDate == "">
                                <#assign dueDate = " - ">
                            <#else>
                                <#assign dueDate = task.dueDate>
                            </#if>
                            <span class="workflow-task-list-label">${msg("tasklist.label.duedate")}:&nbsp;</span>${dueDate}
                        </div>
                        <span class="workflow-task-list-label">${msg("tasklist.label.status")}: </span>${task.statusMessage}
                    </div>
                </div>
                <div class="clear"></div>
            </div>
        </#list>
    </div>

    <#if data.showSubordinateTasks == "true">
        <div class="list-category">
            <div class="list-category-title-subordinate">${msg("tasklist.label.subordinatestasks")}</div>

            <#if data.subordinateTasks?size == 0>
                <div class="workflow-task-line">
                ${msg("tasklist.label.no-tasks")}
                </div>
            </#if>

            <#list data.subordinateTasks as task>
                <div class="workflow-task-item">
                    <div class="workflow-task-list-picture ${task.workflowTaskPriority}" title="${task.priorityMessage}">&nbsp;</div>
                    <div class="left1">
                        <div>
                            <div class="workflow-task-title workflow-task-list-left-column">
                                <a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>
                            </div>
                            <span class="workflow-task-status ${task.type}">${task.typeMessage}</span>
                        </div>
                        <div class="clear"></div>
                        <div class="workflow-task-description">${task.description}</div>
                        <div>
                            <div class="workflow-task-list-left-column">
                                <#if task.dueDate == "">
                                    <#assign dueDate = " - ">
                                <#else>
                                    <#assign dueDate = task.dueDate>
                                </#if>
                                <span class="workflow-task-list-label">${msg("tasklist.label.duedate")}:&nbsp;</span>${dueDate}
                            </div>
                            <span class="workflow-task-list-label">${msg("tasklist.label.status")}: </span>${task.statusMessage}
                        </div>
                    </div>
                    <div class="clear"></div>
                </div>
            </#list>
        </div>
    </#if>

<script type="text/javascript">//<![CDATA[
(function () {
    YAHOO.util.Event.onContentReady("${id}-tasks-states",function (){
        YAHOO.util.Event.on("${id}-tasks-states", "change", onTasksStatesSelectChange, this, true);
        <#if isAnchor == "true">
            YAHOO.util.Dom.get("${id}_tasksList").scrollIntoView();
        </#if>
    });

    function onTasksStatesSelectChange() {
        var statesSelect = Dom.get("${id}-tasks-states");

        var selectedValue = "";
        if (statesSelect != null && statesSelect.value != null) {
            selectedValue = statesSelect.value;
        }

        documentTasksComponent.setTasksState(selectedValue);
        documentTasksComponent.loadTasks();
    }

})();
//]]></script>

</#if>