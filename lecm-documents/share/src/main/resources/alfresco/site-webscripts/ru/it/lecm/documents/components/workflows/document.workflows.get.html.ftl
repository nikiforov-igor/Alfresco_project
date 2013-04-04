<!-- Parameters and libs -->
<#assign el=args.htmlid/>

<#if data??>
<!-- Markup -->
<div class="widget-bordered-panel">
<div class="document-metadata-header document-components-panel">
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
            <#list data.activeWorkflows as workflow>
                <div class="right-workflow">
                    <a href="${url.context}/page/workflow-details?workflowId=${workflow.id}" class="text-cropped" title="${workflow.definition}">${workflow.definition}</a>
                </div>
            </#list>
        </div>

        <#if (data.activeWorkflowsTotalCount > 0 && data.activeWorkflowsTotalCount > data.activeWorkflowsDisplayedCount)>
            <div class="right-more-link-arrow" onclick="documentWorkflowsComponent.onExpand();"></div>
            <div class="right-more-link" onclick="documentWorkflowsComponent.onExpand();">${msg('right.label.more')}</div>
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
</div>
</#if>