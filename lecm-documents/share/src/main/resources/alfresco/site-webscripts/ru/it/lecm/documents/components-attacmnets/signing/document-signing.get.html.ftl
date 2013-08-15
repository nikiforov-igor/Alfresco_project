<#if isDocflowable?? && isDocflowable>
    <!-- Parameters and libs -->
    <#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
    <#assign el=args.htmlid/>
	<#include "/org/alfresco/components/component.head.inc">
	<@script type="text/javascript" src="${page.url.context}/res/scripts/components/document-attachment-signing.js"></@script>

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

<div class="widget-bordered-panel" id="${el}-signing-container" <#if !isSignable>style="display: none;"</#if>>
    <div class="document-metadata-header document-components-panel">
        <h2 id="${el}-signing-heading" class="dark">
				${msg("label.signing")}
                <span class="alfresco-twister-actions">
                    <a id="${el}-viewSignature" href="javascript:void(0)" class="edit" title="${msg("label.view")}"> &nbsp;</a>
                </span>
        </h2>
        <div id="${el}-signing-formContainer">
			<div id="${el}-signDocument" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.sign")}</div>
			<div id="${el}-refreshSignatures" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.refresh.signature")}</div>
			<div id="${el}-uploadSignature" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.upload.signature")}</div>
		</div>
    </div>
</div>

<div class="widget-bordered-panel" id="${el}-exchange-container" <#if !isSignable>style="display: none;"</#if>>
    <div class="document-metadata-header document-components-panel">
        <h2 id="${el}-exchange-heading" class="dark">
            ${msg("label.exchange")}
			<span class="alfresco-twister-actions">
				<a id="${el}-refreshSentDocuments" href="javascript:void(0)" class="edit" title="${msg("label.refresh.sent.document")}"> &nbsp;</a>
			</span>
        </h2>
        <div id="${el}-exchange-formContainer">
			<div id="${el}-sendDocument" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.send")}</div>
			<div class="widget-button-grey text-cropped" href="javascript:void(0);" >${msg("label.read")}</div>
			<div id="${el}-signaturesReceived" class="widget-button-grey text-cropped" href="javascript:void(0);">${msg("label.signatures.received")}</div>
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
				signable: ${isSignable?string}
			}).setMessages(${messages});
		}
		YAHOO.util.Event.on("${el}-signableSwitch", "click", signingComponent.onSignableSwitch, signingComponent, true);
		YAHOO.util.Event.on("${el}-viewSignature", "click", signingComponent.onViewSignature, signingComponent, true);
		YAHOO.util.Event.on("${el}-signDocument", "click", signingComponent.onSignDocument, signingComponent, true);
		YAHOO.util.Event.on("${el}-refreshSignatures", "click", signingComponent.onRefreshSignatures, signingComponent, true);
		YAHOO.util.Event.on("${el}-uploadSignature", "click", signingComponent.onUploadSignature, signingComponent, true);
		YAHOO.util.Event.on("${el}-refreshSentDocuments", "click", signingComponent.onRefreshSentDocuments, signingComponent, true);
		YAHOO.util.Event.on("${el}-sendDocument", "click", signingComponent.onSendDocument, signingComponent, true);
		YAHOO.util.Event.on("${el}-signaturesReceived", "click", signingComponent.onSignaturesReceived, signingComponent, true);
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>
</#if>