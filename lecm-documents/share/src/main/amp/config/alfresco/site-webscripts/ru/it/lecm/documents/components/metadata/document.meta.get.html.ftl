<!-- Document Metadata Header -->
<@markup id="html">
	<#if document??>
	    <!-- Parameters and libs -->
	    <#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
	    <#assign el=args.htmlid/>
	
	<!-- Markup -->
	<div class="widget-bordered-panel metadata-panel">
	    <div id="${el}-wide-view" class="document-metadata-header document-components-panel">
	        <h2 id="${el}-heading" class="dark">
	            ${msg("heading")}
	            <span class="alfresco-twister-actions">
	            <a id="${el}-link" href="javascript:void(0);" class="expand metadata-expand" title="${msg("label.view")}">&nbsp;</a>
	         </span>
	        </h2>
	        <div id="${el}-formContainer"></div>
	    </div>

        <div id="${el}-short-view" class="document-metadata-header document-components-panel short-view hidden">
        <span class="alfresco-twister-actions">
            <a href="javascript:void(0);" class="expand metadata-expand" title="${msg("label.view")}">&nbsp</a>
        </span>
            <div id="${el}-formContainer" class="right-block-content">
            <span class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="${msg('heading')}"></button>
               </span>
            </span>
            </div>
        </div>
	</div>

	<!-- Javascript instance -->
	<script type="text/javascript">//<![CDATA[
    var documentMetadataComponent = null;

	(function(){
        function init() {
            LogicECM.module.Base.Util.loadResources([
                    'scripts/components/document-component-base.js',
                    'components/document-details/document-metadata.js',
                    'scripts/components/document-metadata.js'
                ], [
                    'components/document-details/document-metadata.css',
                    'css/components/document-metadata.css',
                    'css/components/document-metadata-form-edit.css'
            ], create);
        }
        function create() {
            var alfrescoDocumentMetadata = new Alfresco.DocumentMetadata("${el}").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        site: null,
                    formId: <#if formId??>"${formId?js_string}"<#else>null</#if>
                    }).setMessages(${messages});
            documentMetadataComponent = new LogicECM.DocumentMetadata("${el}").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        title: "${msg('heading')}"
                    }).setMessages(${messages});

            // ALFFIVE-32
            // В новой версии DocumentMetadata зачем то есть подписка на metadataRefresh
            // что в свою очередь вызывает странное поведение модуля. Т.к в нашем функционале
            // нету завязки на такую логику - безопаснее будет вырубить её
            // TODO: Выяснить, точно ли нам не нужна такая логика

            YAHOO.Bubbling.unsubscribe("metadataRefresh", alfrescoDocumentMetadata.doRefresh, alfrescoDocumentMetadata);
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
	//]]></script>
	</#if>
</@>