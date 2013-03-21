<!-- Parameters and libs -->
<#assign el=args.htmlid/>
<#if data??>
<!-- Markup -->
<div class="document-metadata-header document-details-panel">
    <h2 id="${el}-heading" class="thin dark">
    ${msg("heading")}
        <span class="alfresco-twister-actions">
            <div class="total-tasks-count-right" <#if data.myTasksTotalCount == 0> style="display: none;" </#if>>${data.myTasksTotalCount}</div>
            <div style="float: right; margin-left: 15px;">
                <a id="${el}-action-expand" href="javascript:void(0);" class="expand" title="${msg("label.expand")}">&nbsp</a>
            </div>
        </span>
    </h2>

    <div id="${el}-formContainer">
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
                    <div class="workflow-task-date">${task.startDate}</div>
                    <div class="workflow-task-status ${task.type}">${task.typeMessage}</div>
                    <div style="clear:both;"></div>
                    <div class="workflow-task-main-text">
                        <span class="workflow-task-title">
                            <a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>
                        </span>&nbsp;${description}
                    </div>
                </div>
            </#list>
        </div>

        <#if (data.myTasksTotalCount > 0 && data.myTasksTotalCount > data.myTasksDisplayedCount)>
            <div class="right-more-link" onclick="documentTasksComponent.onExpand();">${msg('right.label.more')}</div>
            <div class="right-more-link-arrow" onclick="documentTasksComponent.onExpand();"></div>
            <div style="clear:both;"></div>
        </#if>
    </div>

    <script type="text/javascript">
        var documentTasksComponent = null;
    </script>
    <script type="text/javascript">//<![CDATA[
    (function () {
        Alfresco.util.createTwister("${el}-heading", "DocumentTasks");

        function init() {
            documentTasksComponent = new LogicECM.DocumentTasks("${el}").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        title: "${msg('heading')}"
                    }).setMessages(${messages});
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
    </script>
</div>
</#if>
