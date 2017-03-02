<#assign id = args.htmlid?js_string>

<script type="text/javascript">
    function hideButton() {
        if(location.hash != "#expanded") {
            YAHOO.util.Dom.addClass(this, 'hidden');
        }
    }
    YAHOO.util.Event.onAvailable("${id}-action-collapse", hideButton);
</script>

<div class="panel-header">
    <div class="panel-title">${msg("label.title")}</div>
    <div class="lecm-dashlet-actions">
        <a id="${id}-action-collapse" class="collapse" title="${msg("btn.collapse")}"></a>
    </div>
</div>

<#if data??>
<div class="list-container">
    <div class="body scrollableList" id="${id}_results">

        <div class="list-category">
            <div class="list-workflows-title">${msg("tasklist.label.workflows.active")}</div>

            <#list data.activeWorkflows as workflow>
                <div class="workflow-task-item">
                    <div class="workflow-date">${workflow.startDate}</div>
                    <div class="list-workflow-description">
                        <a href="${url.context}/page/workflow-details?workflowId=${workflow.id}">${workflow.definition}</a>
                    </div>
                </div>
            </#list>
        </div>

        <div class="list-category">
            <div class="list-workflows-title">${msg("tasklist.label.workflows.completed")}</div>

            <#list data.completedWorkflows as workflow>
                <div class="workflow-task-item">
                    <div class="workflow-date">${workflow.startDate}</div>
                    <div class="list-workflow-description">
                        <a href="${url.context}/page/workflow-details?workflowId=${workflow.id}">${workflow.definition}</a>
                    </div>
                </div>
            </#list>
        </div>

    </div>
</div>
</#if>