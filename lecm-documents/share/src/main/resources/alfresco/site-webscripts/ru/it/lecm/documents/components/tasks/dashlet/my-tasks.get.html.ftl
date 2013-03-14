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
            <div class="total-tasks-count">${msg("dashlet.tasks.count")}: <span>${data.totalTasksCount}</span></div>
        </span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentTasksComponent.onExpand()" class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
        </span>
    </div>
    <div class="body scrollableList" id="${id}_results">
        <#assign maxMainTextLength = 58>
        <#list data.tasks as task>
            <#assign mainTextLength = task.title?length + task.description?length + 2>
            <#if mainTextLength < maxMainTextLength>
                <#assign description = task.description>
            <#else>
                <#assign descriptionLength = maxMainTextLength - task.title?length - 5>
                <#assign description = task.description?substring(0, descriptionLength)?right_pad(descriptionLength + 3, ".")>
            </#if>
            <div class="my-task">
                <div class="my-task-date">${task.startDate}</div>
                <div class="my-task-status ${task.state}">${task.stateMessage}</div>
                <div class="my-task-main-text">
                    <span class="my-task-title">
                        <a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>
                    </span>&nbsp;${description}
                </div>
            </div>
        </#list>
    </div>
</div>
