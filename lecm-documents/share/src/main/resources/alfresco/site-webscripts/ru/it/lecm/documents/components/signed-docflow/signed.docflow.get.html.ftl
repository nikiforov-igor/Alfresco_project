<#if isDocflowable?? && isDocflowable>
<!-- Signed docflow section start -->
<!-- Parameters and libs -->
<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${page.url.context}/scripts/components/document-signed-docflow.js"></@script>
<#assign el=args.htmlid/>
<!-- Markup -->
<script type="text/javascript">
    var documentSignedDocflowComponent = null;
</script>
<div class="widget-bordered-panel">
	<div class="document-metadata-header document-components-panel">

		<h2 id="${el}-heading" class="dark">
			${msg("heading")}
			<span class="alfresco-twister-actions">
				<a id="${el}-action-refresh" href="javascript:void(0);" onclick="documentSignedDocflowComponent.onRefresh()" class="expand"
				   title="${msg("label.refresh")}">&nbsp</a>
			 </span>
		</h2>

		<div id="${el}-formContainer">
			<div class="widget-button-grey text-cropped" href="javascript:void(0);" onclick="documentSignedDocflowComponent.onSignDocuments()">${msg("label.sign")}</div>
			<div class="widget-button-grey text-cropped" href="javascript:void(0);" onclick="documentSignedDocflowComponent.onSendDocuments()">${msg("label.send")}</div>
			<div class="widget-button-grey text-cropped" href="javascript:void(0);" onclick="documentSignedDocflowComponent.onViewSignatures()">${msg("label.view")}</div>
		</div>
	</div>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		if (documentSignedDocflowComponent == null) {
			documentSignedDocflowComponent = new LogicECM.DocumentSignedDocflow("${el}").setOptions({
						nodeRef: "${nodeRef}",
						title: "${msg('heading')}"
					}).setMessages(${messages});
		}
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>

<!-- Signed docflow section end -->
</#if>
