<#assign id=containerHtmlId/>

<#if data??>
    <#list data.activeWorkflows as workflow>
        <div class="right-workflow">
            <a href="${url.context}/page/workflow-details?workflowId=${workflow.id}" class="text-cropped" title="${workflow.definition}">${workflow.definition}</a>
        </div>
    </#list>

    <#if (data.activeWorkflowsTotalCount > 0 && data.activeWorkflowsTotalCount > data.activeWorkflowsDisplayedCount)>
        <script type="text/javascript">//<![CDATA[
        (function () {
            var moreLinkContainer = YAHOO.util.Dom.get("${id}-right-more-link-container");
            moreLinkContainer.style.display = "block";
        })();
        //]]>
        </script>
    </#if>
</#if>