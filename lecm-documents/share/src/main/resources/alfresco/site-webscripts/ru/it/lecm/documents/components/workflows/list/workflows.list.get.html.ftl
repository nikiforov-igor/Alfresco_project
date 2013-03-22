<#assign id = args.htmlid?js_string>

<#if data??>
<div class="list-container">
    <div class="body scrollableList" id="${id}_results">

        <div class="list-category">
            <div class="list-category-title">${msg("tasklist.label.workflows.active")}</div>

            <#list data.activeWorkflows as workflow>
                <div class="workflow-task-item">
                    <div class="workflow-date">${workflow.startDate}</div>
                    <div class="list-workflow-description">
                        <a href="${url.context}/page/workflow-details?workflowId=${workflow.id}">${workflow.description}</a>
                    </div>
                </div>
            </#list>
        </div>

        <div class="list-category">
            <div class="list-category-title">${msg("tasklist.label.workflows.completed")}</div>

            <#list data.completedWorkflows as workflow>
                <div class="workflow-task-item">
                    <div class="workflow-date">${workflow.startDate}</div>
                    <div class="list-workflow-description">
                        <a href="${url.context}/page/workflow-details?workflowId=${workflow.id}">${workflow.description}</a>
                    </div>
                </div>
            </#list>
        </div>

    </div>
</div>
</#if>