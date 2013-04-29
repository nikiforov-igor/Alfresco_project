<#assign id = args.htmlid>
<#assign containerId = id + "-container">
<#if myTasks??>

    <div class="dashlet contracts bordered">
        <div class="title dashlet-title">
            <span>${msg("label.title")}</span>
        </div>
        <div class="body scrollableList dashlet-body" id="${id}_results">
            <#if myTasks?size == 0>
                <div style="padding-top: 10px;">
                    <div style="float: left;width: 52px;height: 100px;">
                        <img src="${url.context}/res/components/images/help-task-bw-32.png" />
                    </div>
                    <div>
                        <h3 style="font-weight: bold;padding-bottom: 10px;">${msg("empty.title")}</h3>
                    ${msg("empty.description")}
                    </div>
                </div>
            <#else>
                <#assign maxMainTextLength = 58>
                <#list myTasks as task>
                    <#assign mainTextLength = task.title?length + task.description?length + 2>
                    <#if mainTextLength < maxMainTextLength>
                        <#assign description = task.description>
                    <#else>
                        <#assign descriptionLength = maxMainTextLength - task.title?length - 5>
                        <#assign description = task.description?substring(0, descriptionLength)?right_pad(descriptionLength + 3, ".")>
                    </#if>
                    <div class="my-task">
                        <div class="workflow-date">${task.startDate}</div>
                        <div class="workflow-task-status ${task.type}">${task.typeMessage}</div>
                        <div style="clear:both;"></div>
                        <div class="workflow-task-main-text">
                            <span class="workflow-task-title">
                                <a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>
                            </span>&nbsp;${description}
                        </div>
                    </div>
                </#list>
            </#if>
        </div>
    </div>

</#if>
