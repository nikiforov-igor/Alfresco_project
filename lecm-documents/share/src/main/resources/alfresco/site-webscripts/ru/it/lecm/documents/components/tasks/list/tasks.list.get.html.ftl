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

<div class="list-container">
    <div class="tasks-list-filter">
        <span>${msg("tasklist.label.display")}</span>
        <select id="${id}-tasks-states" style="margin-left: 13px;">
            <option selected value="all">${msg("tasklist.option.all")}</option>
            <option ${activeSeleted} value="active">${msg("tasklist.option.active")}</option>
            <option ${completedSeleted} value="completed">${msg("tasklist.option.completed")}</option>
        </select>
    </div>

    <div class="body scrollableList" id="${id}_results">

        <div class="list-category">
            <div class="list-category-title">${msg("tasklist.label.mytasks")}</div>

            <#list data.myTasks as task>
                <div class="workflow-task-item">
                    <div class="workflow-task-list-picture ${task.workflowTaskPriority}" title="${task.priorityMessage}">&nbsp;</div>
                    <div style="float: left;">
                        <div>
                            <div class="workflow-task-title workflow-task-list-left-column" style="font-size: 16px;">
                                <a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>
                            </div>
                            <span class="workflow-task-status ${task.type}">${task.typeMessage}</span>
                        </div>
                        <div style="clear: both;"></div>
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
                    <div style="clear: both;"></div>
                </div>
            </#list>
        </div>

        <#if data.showSubordinateTasks == "true">
            <div class="list-category">
                <div class="list-category-title">${msg("tasklist.label.subordinatestasks")}</div>

                <#list data.subordinateTasks as task>
                    <div class="workflow-task-item">
                        <div class="workflow-task-list-picture ${task.workflowTaskPriority}" title="${task.priorityMessage}">&nbsp;</div>
                        <div style="float: left;">
                            <div>
                                <div class="workflow-task-title workflow-task-list-left-column" style="font-size: 16px;">
                                    <a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>
                                </div>
                                <span class="workflow-task-status ${task.type}">${task.typeMessage}</span>
                            </div>
                            <div style="clear: both;"></div>
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
                        <div style="clear: both;"></div>
                    </div>
                </#list>
            </div>
        </#if>

    </div>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
    YAHOO.util.Event.onDOMReady(function (){
        YAHOO.util.Event.on("${id}-tasks-states", "change", onTasksStatesSelectChange, this, true);
    });

    function onTasksStatesSelectChange() {
        var statesSelect = Dom.get("${id}-tasks-states");

        var selectedValue = "";
        if (statesSelect != null && statesSelect.value != null) {
            selectedValue = statesSelect.value;
        }

        documentTasksComponent.setTasksState(selectedValue);
        documentTasksComponent.onExpand();
    }

})();
//]]></script>

</#if>