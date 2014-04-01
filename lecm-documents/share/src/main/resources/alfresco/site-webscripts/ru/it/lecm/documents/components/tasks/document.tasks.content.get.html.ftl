<#assign id=containerHtmlId/>

<h2 id="${id}-heading" class="dark">
${msg("heading")}
    <span class="alfresco-twister-actions">
        <a id="${id}-action-expand" href="javascript:void(0);" class="expand" title="${msg("label.expand")}">&nbsp;</a>
    </span>
</h2>

<#--<div style="position: relative;">-->
    <#--<div class="total-tasks-count-right" <#if data.myTasksTotalCount == 0> style="display: none;" </#if>>${data.myTasksTotalCount}</div>-->
<#--</div>-->

<div id="${id}-formContainer">
<#if data??>
    <div class="right-tasks-container">
        <#assign maxMainTextLength = 53>
        <#list data.myTasks as task>
            <#assign mainTextLength = task.title?length + task.description?length + 2>
            <#if mainTextLength < maxMainTextLength>
                <#assign description = task.description>
            <#else>
                <#assign descriptionLength = maxMainTextLength - task.title?length - 5>
                <#assign description = task.description?substring(0, descriptionLength)?right_pad(descriptionLength + 3, ".")>
            </#if>
            <div class="right-task">
                <div class="workflow-date">${task.startDate}</div>
                <div class="workflow-task-status ${task.type}">${task.typeMessage}</div>
                <div style="clear:both;"></div>
                <div class="workflow-task-main-text text-broken">
                    <span class="workflow-task-title">
                        <#--<a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}</a>-->
                        <a href="javascript:void(0);" onclick="LogicECM.DocumentTasks.loadTask('${task.id}', '${task.title}');">${task.title}</a>
                    </span>
                </div>
            </div>
        </#list>
    </div>

    <#if (data.myTasksTotalCount > 0 && data.myTasksTotalCount > data.myTasksDisplayedCount)>
        <div class="right-more-link-arrow" onclick="documentTasksComponentContent.onExpand();"></div>
        <div class="right-more-link" onclick="documentTasksComponentContent.onExpand();">${msg('right.label.more')}</div>
        <div style="clear:both;"></div>
    </#if>
</#if>
</div>

<script type="text/javascript">
    var documentTasksComponentContent = null;
</script>
<script type="text/javascript">//<![CDATA[
(function () {

    function initDocumentTasks() {
        Alfresco.util.createTwister("${id}-heading", "DocumentTasksContent", {
            panel: "${id}-formContainer"
        });

        documentTasksComponentContent = new LogicECM.DocumentTasks("${id}").setOptions(
            {
                nodeRef: "${args.nodeRef}",
                title: "${msg('heading')}"
            }).setMessages(${messages});
    }

    YAHOO.util.Event.onContentReady("${id}-formContainer", initDocumentTasks);
})();
//]]>
</script>