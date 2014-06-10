<#if isDocflowable?? && isDocflowable>
<!-- Signed docflow section start -->
<!-- Parameters and libs -->
<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${url.context}/res/scripts/components/document-signed-docflow.js"></@script>
<#assign el=args.htmlid/>

<#attempt>
	<#import "/ru/it/lecm/signed/docflow/components/crypto.ftl" as crypto/>
	<@crypto.initApplet/>
<#recover>
</#attempt>

<div class="widget-bordered-panel">
	<div class="document-metadata-header document-components-panel">

		<h2 id="${el}-heading" class="dark">
			${msg("heading")}
			<span class="alfresco-twister-actions">
				<a id="${el}-action-refresh" href="javascript:void(0);" class="refresh" title="${msg("label.refresh")}">&nbsp</a>
			 </span>
		</h2>

		<div id="${el}-formContainer">
			<div id="${el}-signDocuments" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.sign")}</div>
			<div id="${el}-sendDocuments" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.send")}</div>
			<div id="${el}-viewSignatures" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.view")}</div>
		</div>
	</div>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		var isFunction = YAHOO.lang.isFunction;
		if (isFunction(LogicECM.DocumentSignedDocflow)) {
			var signingComponent = new LogicECM.DocumentSignedDocflow("${el}").setOptions({
				nodeRef: "${nodeRef}",
				title: "${msg('heading')}"
			}).setMessages(${messages});
			YAHOO.util.Event.on("${el}-action-refresh", "click", signingComponent.onRefresh, signingComponent, true);
			YAHOO.util.Event.on("${el}-signDocuments", "click", signingComponent.onSignDocuments, signingComponent, true);
			YAHOO.util.Event.on("${el}-sendDocuments", "click", signingComponent.onSendDocuments, signingComponent, true);
			YAHOO.util.Event.on("${el}-viewSignatures", "click", signingComponent.onViewSignatures, signingComponent, true);
		}
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>

<!-- Signed docflow section end -->
</#if>
