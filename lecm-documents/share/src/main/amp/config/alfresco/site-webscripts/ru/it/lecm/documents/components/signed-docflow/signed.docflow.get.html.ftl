<#if isDocflowable?? && isDocflowable>
<!-- Signed docflow section start -->
<!-- Parameters and libs -->
<#include "/org/alfresco/components/component.head.inc">
<#assign el=args.htmlid/>

<#attempt>
	<#import "/ru/it/lecm/signed/docflow/components/crypto.ftl" as crypto/>
	<@crypto.initApplet/>
<#recover>
</#attempt>

<div class="widget-bordered-panel signed-docflow-panel">
	<div id="${el}-wide-view" class="document-metadata-header document-components-panel">

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
    <div id="${el}-short-view" class="document-components-panel short-view hidden">
        <div id="${el}-formContainer" class="right-block-content">
            <span class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="${msg('heading')}"></button>
               </span>
            </span>
        </div>
    </div>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
        LogicECM.module.Base.Util.loadResources([
        		'scripts/components/document-signed-docflow.js'
            ], [
             	'css/components/document-signed-docflow.css'
            ], create);
    }

	function create() {
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

    LogicECM.services = LogicECM.services || {};
    if (LogicECM.services.documentViewPreferences) {
        var shortView = LogicECM.services.documentViewPreferences.getShowRightPartShort();
        if (shortView) {
            Dom.addClass("${el}-wide-view", "hidden");
            Dom.removeClass("${el}-short-view", "hidden");
        }
    }
})();
//]]>
</script>

<!-- Signed docflow section end -->
</#if>
