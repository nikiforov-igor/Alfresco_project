<#assign id=containerHtmlId/>

<h2 id="${id}-heading" class="dark">
    ${msg("heading")}
    <#--<#if data??>-->
        <#--<span class="total-errands-count-right <#if data.errands?size == 0>hidden1</#if>">${data.errandsCount?string}</span>-->
    <#--</#if>-->
    <span class="alfresco-twister-actions">
        <a id="${id}-action-expand" href="#" onclick="return false;" class="expand errands-expand" title="${msg("label.expand")}">&nbsp;</a>
    </span>
</h2>

<div id="${id}-formContainer">
<#if data??>
    <div class="right-tasks-container">
	    <#if data?? && data.errands?? && (data.errands?size > 0)>
	        <#assign maxMainTextLength = 53>
	        <#list data.errands as errand>
	            <div class="right-errand">
                    <div class="workflow-task-main-text text-broken">
	                    <span class="errand-title">
	                        <a href="${url.context}/page/document?nodeRef=${errand.nodeRef}">${msg('label.from')}: ${errand.initiatorName}, ${msg('label.date')}: ${errand.limitationDateText}</a>
	                    </span>
                    </div>
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

    <#if (data.errands?size > 0 && (data.errandsCount > data.errands?size))>
        <div class="right-more-link-arrow" onclick="documentErrandsComponent.onExpand();"></div>
        <div class="right-more-link" onclick="documentErrandsComponent.onExpand();">${msg('right.label.more')}</div>
        <div class="clear"></div>
    </#if>
</#if>
</div>

<script type="text/javascript">
    //variable is used for expanding dashlet. refactor it?
    var documentErrandsComponent = null;
    var errandsComponent = null;
</script>

<script type="text/javascript">//<![CDATA[
(function () {
    LogicECM.module.Documents.ERRANDS_SETTINGS = LogicECM.module.Documents.ERRANDS_SETTINGS || <#if errandsSettings?? >${errandsSettings?string}<#else>{}</#if>;

    function initComponent() {
        Alfresco.util.createTwister("${id}-heading", "DocumentErrands", {
            panel: "${id}-formContainer"
        });

        documentErrandsComponent = new LogicECM.DocumentErrands("${id}").setOptions({
			nodeRef: "${args.nodeRef}",
			title: "${msg('heading')}"
		}).setMessages(${messages});

        errandsComponent = new LogicECM.module.Errands.dashlet.Errands("${id}").setOptions({
			itemType: "lecm-errands:document",
			destination: LogicECM.module.Documents.ERRANDS_SETTINGS.nodeRef,
			parentDoc: "${nodeRef}"
        <#if subjectAssoc??>,
            parentDocSubjectAssoc: "${subjectAssoc}"
        </#if>
		}).setMessages(${messages});
    }

    YAHOO.util.Event.onDOMReady(function () {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-errands/lecm-errands-dashlet.js',
            'scripts/lecm-errands/document-errands.js'
        ],[], initComponent);
    });
})();
//]]>
</script>
