<!-- Parameters and libs -->
<#assign el=args.htmlid/>

<#if data??>
<!-- Markup -->
<div class="document-metadata-header document-details-panel">
    <h2 id="${el}-heading" class="thin dark">
    ${msg("heading")}
        <span class="alfresco-twister-actions">
            <div>
                <a id="${el}-action-expand" href="javascript:void(0);" class="expand" title="${msg("label.expand")}">&nbsp</a>
            </div>
        </span>
    </h2>

    <div id="${el}-formContainer">
        <div class="right-workflows-container">
            <#assign maxDescriptionLength = 30>
            <#list data.activeWorkflows as workflow>
                <#if (workflow.description?length <= maxDescriptionLength)>
                    <#assign description = workflow.description>
                <#else>
                    <#assign description = workflow.description?substring(0, maxDescriptionLength - 3)?right_pad(maxDescriptionLength, ".")>
                </#if>
                <div class="right-workflow">
                    <a href="${url.context}/page/workflow-details?workflowId=${workflow.id}">${description}</a>
                </div>
            </#list>
        </div>

        <#if (data.activeWorkflowsTotalCount > 0 && data.activeWorkflowsTotalCount > data.activeWorkflowsDisplayedCount)>
            <div class="right-more-link" onclick="documentWorkflowsComponent.onExpand();">${msg('right.label.more')}</div>
            <div class="right-more-link-arrow" onclick="documentWorkflowsComponent.onExpand();"></div>
            <div style="clear:both;"></div>
        </#if>
    </div>

    <script type="text/javascript">
        var documentWorkflowsComponent = null;
    </script>
    <script type="text/javascript">//<![CDATA[
    (function () {
        Alfresco.util.createTwister("${el}-heading", "DocumentWorkflows");

        function init() {
            documentWorkflowsComponent = new LogicECM.DocumentWorkflows("${el}").setOptions(
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