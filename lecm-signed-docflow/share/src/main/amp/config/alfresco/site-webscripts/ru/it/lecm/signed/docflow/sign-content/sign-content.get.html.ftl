<#if isSignable?? && isSignable>

<!-- Parameters and libs -->
<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
<#assign el=args.htmlid/>
<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${url.context}/res/scripts/signed-docflow/sign-content.js"></@script>

<#attempt>
	<#import "/ru/it/lecm/signed/docflow/components/crypto.ftl" as crypto/>
	<@crypto.initApplet/>
<#recover>
</#attempt>

<div class="document-details-panel document-actions" id="${el}-signing-container">
	<h2 id="${el}-signing-heading" class="thin dark">
			${msg("label.signing")}
			<span class="alfresco-twister-actions">
				<a id="${el}-viewSignature" href="javascript:void(0)" class="view-signatures" title="${msg("label.view")}">&nbsp;</a>
			</span>
	</h2>
	<div id="${el}-signing-formContainer" class="doclist">
		<div class="action-set">
			<div><a id="${el}-signDocument" href="javascript:void(0);" class="action-link">${msg("label.sign")}</a></div>
			<div><a id="${el}-refreshSignatures" href="javascript:void(0);" class="action-link">${msg("label.refresh.signature")}</a></div>
			<div><a id="${el}-uploadSignature" href="javascript:void(0);" class="action-link">${msg("label.upload.signature")}</a></div>
			<div><a id="${el}-exportSignature" href="javascript:void(0);" class="action-link">${msg("label.export.signature")}</a></div>
		</div>
	</div>
</div>

<div class="document-details-panel document-actions" id="${el}-exchange-container">
	<h2 id="${el}-exchange-heading" class="thin dark">
		${msg("label.exchange")}
		<span class="alfresco-twister-actions">
			<a id="${el}-refreshSentDocuments" href="javascript:void(0)" class="refresh-sent-document" title="${msg("label.refresh.sent.document")}">&nbsp;</a>
		</span>
	</h2>
	<div id="${el}-exchange-formContainer" class="doclist">
		<div class="action-set">
			<div><a id="${el}-sendDocument" href="javascript:void(0);" class="action-link">${msg("label.send")}</a></div>
			<div>
				<span id="${el}-readState" style="background-position: 4px 1px; background-repeat: no-repeat; border: 1px solid transparent; display: block; min-height: 16px; padding: 2px 0 2px 24px;">${msg("label.read")}
					<i style="float: right; margin-right: 10px;"></i>
				</span>
			</div>
			<div>
				<a id="${el}-signaturesReceived" href="javascript:void(0);" class="action-link">${msg("label.signatures.received")}
					<span id="${el}-receivedCount" style="float: right; margin-right: 10px; font-weight: bold;"></span>
				</a>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		var isFunction = YAHOO.lang.isFunction;
		if (isFunction(LogicECM.ContentSigning)) {
			var signingComponent = new LogicECM.ContentSigning("${el}").setOptions({
				nodeRef: "${nodeRef}",
				title: "${msg('heading')}"
			}).setMessages(${messages});
			YAHOO.util.Event.on("${el}-viewSignature", "click", signingComponent.onViewSignature, signingComponent, true);
			YAHOO.util.Event.on("${el}-signDocument", "click", signingComponent.onSignDocument, signingComponent, true);
			YAHOO.util.Event.on("${el}-refreshSignatures", "click", signingComponent.onRefreshSignatures, signingComponent, true);
			YAHOO.util.Event.on("${el}-uploadSignature", "click", signingComponent.onUploadSignature, signingComponent, true);
			YAHOO.util.Event.on("${el}-exportSignature", "click", signingComponent.onExportSignature, signingComponent, true);
			YAHOO.util.Event.on("${el}-refreshSentDocuments", "click", signingComponent.onRefreshSentDocuments, signingComponent, true);
			YAHOO.util.Event.on("${el}-sendDocument", "click", signingComponent.onSendDocument, signingComponent, true);
			YAHOO.util.Event.on("${el}-signaturesReceived", "click", signingComponent.onSignaturesReceived, signingComponent, true);
		}
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>
</#if>
