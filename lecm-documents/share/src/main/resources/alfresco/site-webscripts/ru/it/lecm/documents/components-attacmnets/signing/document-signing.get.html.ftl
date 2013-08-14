<#if isDocflowable?? && isDocflowable>
    <!-- Parameters and libs -->
    <#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
    <#assign el=args.htmlid/>
	<#include "/org/alfresco/components/component.head.inc">
	<@script type="text/javascript" src="${page.url.context}/res/scripts/components/document-attachment-signing.js"></@script>

    <!-- Markup -->
<script type="text/javascript">
    var documentAtachmentSigningComponent = null;
	function afterLoad(){
		cryptoAppletModule.startApplet();
	}
</script>
<applet 
	codebase="/share/scripts/signed-docflow"
	code="ru.businesslogic.crypto.userinterface.CryptoApplet.class" 
    archive="/share/scripts/signed-docflow/ITStampApplet.jar"
    name="signApplet"
    width=1 
    height=1>
	<param name="signOnLoad" value="false"/>
	<param name="debug" value="true"/>
	<param name="providerType" value="CSP_CRYPTOPRO"/>
	<param name="doAfterLoad" value="true"/>
</applet>
<div class="widget-bordered-panel">
    <div class="document-metadata-header document-components-panel">
        <h2 id="${el}-checkbox-heading" class="dark">
            ${msg("label.signable")}&nbsp;
            <input type="checkbox" onclick="documentAtachmentSigningComponent.onSignableSwitch(null, { checkbox: this } )" <#if isSignable>checked</#if> style="vertical-align: middle;">
        </h2>
    </div>
</div>

<div class="widget-bordered-panel" id="${el}-signing-container" <#if !isSignable>style="display: none;"</#if>>
    <div class="document-metadata-header document-components-panel">
        <h2 id="${el}-signing-heading" class="dark">
				${msg("label.signing")}
                <span class="alfresco-twister-actions">
                    <a href="javascript:void(0)" onclick="documentAtachmentSigningComponent.onViewSignature()" class="edit" title="${msg("label.view")}"> &nbsp;</a>
                </span>
        </h2>
        <div id="${el}-signing-formContainer">
			<div class="widget-button-grey text-cropped" href="javascript:void(0);" onclick="documentAtachmentSigningComponent.onSignDocument()">${msg("label.sign")}</div>
			<div class="widget-button-grey text-cropped" href="javascript:void(0);" onclick="documentAtachmentSigningComponent.onRefreshSignatures()">${msg("label.refresh.signature")}</div>
			<div class="widget-button-grey text-cropped" href="javascript:void(0);" onclick="documentAtachmentSigningComponent.onUploadSignature()">${msg("label.upload.signature")}</div>
		</div>
    </div>
</div>

<div class="widget-bordered-panel" id="${el}-exchange-container" <#if !isSignable>style="display: none;"</#if>>
    <div class="document-metadata-header document-components-panel">
        <h2 id="${el}-exchange-heading" class="dark">
            ${msg("label.exchange")}
			<span class="alfresco-twister-actions">
				<a href="javascript:void(0)" class="edit" onclick="documentAtachmentSigningComponent.onRefreshSentDocuments()" title="${msg("label.refresh.sent.document")}"> &nbsp;</a>
			</span>
        </h2>
        <div id="${el}-exchange-formContainer">
			<div class="widget-button-grey text-cropped" href="javascript:void(0);" onclick="documentAtachmentSigningComponent.onSendDocument()">${msg("label.send")}</div>
			<div class="widget-button-grey text-cropped" href="javascript:void(0);" >${msg("label.read")}</div>
			<div class="widget-button-grey text-cropped" href="javascript:void(0);" onclick="documentAtachmentSigningComponent.onSignaturesReceived()">${msg("label.signatures.received")}</div>
		</div>
    </div>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		if (documentAtachmentSigningComponent == null) {
			documentAtachmentSigningComponent = new LogicECM.DocumentAttachmentSigning("${el}").setOptions({
						nodeRef: "${nodeRef}",
						title: "${msg('heading')}",
						signable: ${isSignable?string}
					}).setMessages(${messages});
		}
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>
</#if>