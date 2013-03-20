<#assign id = args.htmlid?js_string>

<#assign activeSeleted = "">
<#if tasksType == "active">
    <#assign activeSeleted = "selected">
</#if>

<#assign completedSeleted = "">
<#if tasksType == "completed">
    <#assign completedSeleted = "selected">
</#if>

<div class="workflow-tasks-container">
    <div>
        <span>${msg("tasklist.label.display")}</span>
        <select id="${id}-tasks-types" style="margin-left: 13px;">
            <option selected value="all">${msg("tasklist.option.all")}</option>
            <option ${activeSeleted} value="active">${msg("tasklist.option.active")}</option>
            <option ${completedSeleted} value="completed">${msg("tasklist.option.completed")}</option>
        </select>
    </div>

    <div class="body scrollableList" id="${id}_results">

        <div class="workflow-tasks-category">
            <div class="workflow-tasks-category-title">${msg("tasklist.label.mytasks")}</div>

            <#list data.myTasks as task>
                <div class="workflow-task">
                    <div class="workflow-task-list-picture ${task.workflowTaskPriority}" title="${task.priorityMessage}">&nbsp;</div>
                    <div style="float: left;">
                        <div>
                            <div class="workflow-task-title workflow-task-list-left-column" style="font-size: 16px;">
                                <a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>
                            </div>
                            <span class="workflow-task-status ${task.type}">${task.typeMessage}</span>
                        </div>
                        <div style="clear: both;"></div>
                        <div class="workflow-description">${task.description}</div>
                        <div>
                            <div class="workflow-task-list-left-column">
                                <span class="workflow-task-list-label">${msg("tasklist.label.duedate")}:&nbsp;</span>${task.dueDate}
                            </div>
                            <span class="workflow-task-list-label">${msg("tasklist.label.status")}: </span>${task.statusMessage}
                        </div>
                    </div>
                    <div style="clear: both;"></div>
                </div>
            </#list>
        </div>

        <#if data.showSubordinateTasks == "true">
            <div class="workflow-tasks-category">
                <div class="workflow-tasks-category-title">${msg("tasklist.label.subordinatestasks")}</div>

                <#list data.subordinateTasks as task>
                    <div class="workflow-task">
                        <div class="workflow-task-list-picture ${task.workflowTaskPriority}" title="${task.priorityMessage}">&nbsp;</div>
                        <div style="float: left;">
                            <div>
                                <div class="workflow-task-title workflow-task-list-left-column" style="font-size: 16px;">
                                    <a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>
                                </div>
                                <span class="workflow-task-status ${task.type}">${task.typeMessage}</span>
                            </div>
                            <div style="clear: both;"></div>
                            <div class="workflow-description">${task.description}</div>
                            <div>
                                <div class="workflow-task-list-left-column">
                                    <span class="workflow-task-list-label">${msg("tasklist.label.duedate")}:&nbsp;</span>${task.dueDate}
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
        YAHOO.util.Event.on("${id}-tasks-types", "change", onTasksTypesSelectChange, this, true);
    });

    function onTasksTypesSelectChange() {
        var typesSelect = Dom.get("${id}-tasks-types");

        var selectValue = "";
        if (typesSelect != null && typesSelect.value != null) {
            selectValue = typesSelect.value;
        }

        documentTasksComponent.setTasksType(selectValue);
        documentTasksComponent.onExpand();
    }

})();
//]]></script>