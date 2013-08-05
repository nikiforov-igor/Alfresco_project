<#if isSignable?? && isSignable>

<!-- Parameters and libs -->
<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
<#assign el=args.htmlid/>
<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${page.url.context}/res/scripts/signed-docflow/sign-content.js"></@script>

    <!-- Markup -->
<script type="text/javascript">
    var contentAtachmentSigningComponent = null;
</script>

<div class="document-details-panel document-actions" id="${el}-signing-container">
	<h2 id="${el}-signing-heading" class="thin dark">
			${msg("label.signing")}
			<span class="alfresco-twister-actions">
				<a href="javascript:void(0)" onclick="contentAtachmentSigningComponent.onViewSignature()" class="edit" title="${msg("label.view")}"> &nbsp;</a>
			</span>
	</h2>
	<div id="${el}-signing-formContainer" class="doclist">
		<div class="action-set">
			<div><a href="javascript:void(0);" class="action-link" onclick="contentAtachmentSigningComponent.onSignDocument()"> ${msg("label.sign")}</a></div>
			<div><a href="javascript:void(0);" class="action-link" onclick="contentAtachmentSigningComponent.onRefreshSignatures()"> ${msg("label.refresh.signature")}</a></div>
			<div><a href="javascript:void(0);" class="action-link" onclick="contentAtachmentSigningComponent.onUploadSignature()"> ${msg("label.upload.signature")}</a></div>
		</div>
	</div>
</div>

<div class="document-details-panel document-actions" id="${el}-exchange-container">
	<h2 id="${el}-exchange-heading" class="thin dark">
		${msg("label.exchange")}
		<span class="alfresco-twister-actions">
			<a href="javascript:void(0)" class="edit" onclick="contentAtachmentSigningComponent.onRefreshSentDocuments()" title="${msg("label.refresh.sent.document")}"> &nbsp;</a>
		</span>
	</h2>
	<div id="${el}-exchange-formContainer" class="doclist">
		<div class="action-set">
			<div><a href="javascript:void(0);" class="action-link" onclick="contentAtachmentSigningComponent.onSendDocument()"> ${msg("label.send")}</a></div>
			<div><a href="javascript:void(0);" class="action-link"> ${msg("label.read")}</a></div>
			<div><a href="javascript:void(0);" class="action-link" onclick="contentAtachmentSigningComponent.onSignaturesReceived()"> ${msg("label.signatures.received")}</a></div>
		</div>
	</div>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		if (contentAtachmentSigningComponent == null) {
			contentAtachmentSigningComponent = new LogicECM.ContentSigning("${el}").setOptions({
						nodeRef: "${nodeRef}",
						title: "${msg('heading')}"
					}).setMessages(${messages});
		}
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>
</#if>
