<#if isDocflowable?? && isDocflowable>
    <!-- Parameters and libs -->
    <#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
    <#assign el=args.htmlid/>
	<#include "/org/alfresco/components/component.head.inc">
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-component-base.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-attachment-signing.js"></@script>

<#attempt>
	<#import "/ru/it/lecm/signed/docflow/components/crypto.ftl" as crypto/>
	<@crypto.initApplet/>
<#recover>
</#attempt>

<div class="widget-bordered-panel">
    <div class="document-metadata-header document-components-panel">
        <h2 id="${el}-checkbox-heading" class="dark">
            ${msg("label.signable")}&nbsp;
            <input id="${el}-signableSwitch" type="checkbox" style="vertical-align: middle;" <#if isSignable>checked</#if> <#if isSigned>disabled="true"</#if>>
        </h2>
    </div>
</div>

<div class="widget-bordered-panel <#if !isSignable>hidden1</#if>" id="${el}-signing-container">
    <div class="document-metadata-header document-components-panel">
        <h2 id="${el}-signing-heading" class="dark">
				${msg("label.signing")}
                <span class="alfresco-twister-actions">
                    <a id="${el}-viewSignature" href="javascript:void(0)" class="view-signatures" title="${msg("label.view")}"> &nbsp;</a>
                </span>
        </h2>
        <div id="${el}-signing-formContainer">
			<div id="${el}-signDocument" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.sign")}</div>
			<div id="${el}-refreshSignatures" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.refresh.signature")}</div>
			<div id="${el}-uploadSignature" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.upload.signature")}</div>
            <div id="${el}-exportSignature" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.export.signature")}</div>
            <input type="file" id="${el}-localSign" style="display:none">
		</div>
    </div>
</div>

<div class="widget-bordered-panel <#if !isSignable || !isExchangeEnabled>hidden1</#if>" id="${el}-exchange-container">
    <div class="document-metadata-header document-components-panel">
        <h2 id="${el}-exchange-heading" class="dark">
            ${msg("label.exchange")}
			<span class="alfresco-twister-actions">
				<a id="${el}-refreshSentDocuments" href="javascript:void(0)" class="refresh-sent-document" title="${msg("label.refresh.sent.document")}"> &nbsp;</a>
			</span>
        </h2>
        <div id="${el}-exchange-formContainer">
			<div id="${el}-sendDocument" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.send")}</div>

			<div class="text-cropped" style="font: 14px Arial; margin: 5px 0; padding: 3px 12px; text-decoration: none;">${msg("label.read")}
				<span id="${el}-readState">
					<i style="float: right; margin-right: -5px;"></i>
				</span>
			</div>

			<div id="${el}-signaturesReceived" href="javascript:void(0);" class="widget-button-grey text-cropped">${msg("label.signatures.received")}
				<span id="${el}-receivedCount" style="float: right; margin-right: -5px; font-weight: bold;"></span>
			</div>
		</div>
    </div>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		var isFunction = YAHOO.lang.isFunction;
		if (isFunction(LogicECM.DocumentAttachmentSigning)) {
			var signingComponent = new LogicECM.DocumentAttachmentSigning("${el}").setOptions({
                nodeRef: "${nodeRef}",
                title: "${msg('heading')}",
                signable: ${isSignable?string},
                isExchangeEnabled: ${isExchangeEnabled?string}
			}).setMessages(${messages});
		}
		YAHOO.util.Event.on("${el}-signableSwitch", "click", signingComponent.onSignableSwitch, signingComponent, true);
		YAHOO.util.Event.on("${el}-viewSignature", "click", signingComponent.onViewSignature, signingComponent, true);
		YAHOO.util.Event.on("${el}-signDocument", "click", signingComponent.onSignDocument, signingComponent, true);
		YAHOO.util.Event.on("${el}-refreshSignatures", "click", signingComponent.onRefreshSignatures, signingComponent, true);
		YAHOO.util.Event.on("${el}-uploadSignature", "click", signingComponent.onUploadSignature, signingComponent, true);
        YAHOO.util.Event.on("${el}-localSign", "change", signingComponent.handleClientLocalSign, signingComponent, true);
		YAHOO.util.Event.on("${el}-refreshSentDocuments", "click", signingComponent.onRefreshSentDocuments, signingComponent, true);
		YAHOO.util.Event.on("${el}-sendDocument", "click", signingComponent.onSendDocument, signingComponent, true);
		YAHOO.util.Event.on("${el}-signaturesReceived", "click", signingComponent.onSignaturesReceived, signingComponent, true);
        YAHOO.util.Event.on("${el}-exportSignature", "click", signingComponent.onExportSignature, signingComponent, true);
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>
</#if>