<#assign id = args.htmlid?js_string>

<script type="text/javascript">
    //<![CDATA[
    (function() {
        function init() {
            new Alfresco.widget.DashletResizer("${id}", "document.tasks.dashlet");
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="dashlet document bordered">
    <div class="title dashlet-title">
        <span>
            <div style="float:left; margin-right: 4px;">${msg("label.title")}</div>
            <div class="dashlet-task-total-tasks">${msg("dashlet.tasks.count")}: <span class="dashlet-task-total-tasks-count">${data.count}</span></div>
        </span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentTasksComponent.onExpand()" class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
        </span>
    </div>
    <div class="body scrollableList" id="${id}_results">
        <#assign maxMainTextLength = 60>
        <#list data.tasks as task>
            <#assign mainTextLength = task.title?length + task.description?length + 2>
            <#if mainTextLength < maxMainTextLength>
                <#assign description = task.description>
            <#else>
                <#assign descriptionLength = maxMainTextLength - task.title?length - 5>
                <#assign description = task.description?substring(0, descriptionLength)?right_pad(descriptionLength + 3, ".")>
            </#if>
            <div class="dashlet-task">
                <div class="dashlet-task-date">${task.startDate}</div>
                <div class="dashlet-task-status ${task.state}">${task.stateMessage}</div>
                <div class="dashlet-task-main-text">
                    <span class="dashlet-task-title">
                        <a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>
                    </span>&nbsp;${description}
                </div>
            </div>
        </#list>
    </div>
</div>