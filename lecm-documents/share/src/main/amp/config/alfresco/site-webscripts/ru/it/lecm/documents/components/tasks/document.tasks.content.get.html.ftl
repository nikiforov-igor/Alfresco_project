<@script type="text/javascript" src="${url.context}/res/scripts/components/document-tasks.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-tasks.css" />

<#assign id=containerHtmlId/>

<h2 id="${id}-heading" class="dark">
${msg("heading")}
    <span class="alfresco-twister-actions">
        <a id="${id}-action-expand" href="javascript:void(0);" class="expand" title="${msg("label.expand")}">&nbsp;</a>
    </span>
</h2>

<#if data??>
<div>
    <div class="total-tasks-count-right <#if data.errands?size == 0>hidden1</#if>">${data.errands?size}</div>
</div>
</#if>

<div id="${id}-formContainer">
<#if data??>
    <div class="right-tasks-container">
	    <#if data?? && data.errands?? && (data.errands?size > 0)>
	        <#assign maxMainTextLength = 53>
	        <#list data.errands as task>
	            <div class="right-task">
                    <div class="workflow-task-main-text text-broken">
	                    <span class="workflow-task-title">
	                        <a href="${url.context}/page/document?nodeRef=${task.nodeRef}">${msg('label.from')}: ${task.initiatorName}</a>
	                    </span>
                    </div>
                    <div class="workflow-date">${task.dueDate}</div>
                </div>
	        </#list>
		<#else>
			<div class="block-empty-body">
			    <span class="block-empty faded">
			    ${msg("message.block.empty")}
			    </span>
			</div>
		</#if>
    </div>

    <#if (data.errands?size > 0 && data.errands?size > 5)>
        <div class="right-more-link-arrow" onclick="documentTasksComponentContent.onExpand();"></div>
        <div class="right-more-link" onclick="documentTasksComponentContent.onExpand();">${msg('right.label.more')}</div>
        <div class="clear"></div>
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