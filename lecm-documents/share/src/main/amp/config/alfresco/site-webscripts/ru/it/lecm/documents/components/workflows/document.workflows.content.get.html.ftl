<#assign id=containerHtmlId/>

<#if data??>
	<#if data.activeWorkflows?? && (data.activeWorkflows?size > 0)>
	    <#list data.activeWorkflows as workflow>
	        <div class="right-workflow">
	            <a href="${url.context}/page/workflow-details?workflowId=${workflow.id}" class="text-cropped" title="${workflow.definition}">${workflow.definition}</a>
	        </div>
	    </#list>
	<#else>
		<div class="block-empty-body">
		    <span class="block-empty faded">
			    ${msg("message.block.empty")}
		    </span>
		</div>
	</#if>

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