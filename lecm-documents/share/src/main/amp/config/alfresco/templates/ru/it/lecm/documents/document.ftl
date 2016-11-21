<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader>
	<@script type="text/javascript" src="${url.context}/res/components/documentlibrary/actions.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/yui/resize/resize.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/components/document-details/document-metadata.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/dashlets/lecm-errands-dashlet.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/graph-tree-control.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/graph-tree.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/lecm-document-errands.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/lecm-document-ajax-content.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/lecm-document-save-last.js"></@script>

    <#-- Скрипт для валидатора. Было решено вставить сюда, чтобы хотя бы не тащить вообще на все страницы -->
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/lecm-document-regnum-uniqueness-validator.js"></@script>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/actions.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-documents/graph-view-control.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/document-details/document-metadata.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form.css" />

    <#-- TODO: IMPORTANT for IE ! -->
    <#-- В IE9 следующие файлы подключаются только при использовании тэга <link>,
        при подключении макросом - не работают.
        Причина не выявлена, возможно, как-то связано с местом расположения файлов (/css/...)
    -->
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/light-blue-bgr.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/document-components-panel.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/dashlet-components.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/page-document.css" />

    <#if documentType?? && documentType == "{http://www.it.ru/logicECM/errands/1.0}document">
        <link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-metadata.css" />
    </#if>
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-form.css" />

    <@templateHtmlEditorAssets />
    <script type="text/javascript">//<![CDATA[
        if (typeof LogicECM == "undefined" || !LogicECM) {
			LogicECM = {};
		}
		LogicECM.module = LogicECM.module || {};
		LogicECM.module.Documents = LogicECM.module.Documents|| {};
		(function() {
			LogicECM.module.Documents.ERRANDS_SETTINGS = LogicECM.module.Documents.ERRANDS_SETTINGS || <#if errandsSettings?? >${errandsSettings}<#else>{}</#if>;

            var viewHistory = new LogicECM.module.Document.ViewHistory("save-view-history").setOptions({
                nodeRef: "${page.url.args.nodeRef}"
            });

            viewHistory.save();

		})();
    //]]></script>
	</@>

<@templateBody>
<div id="alf-hd">
	<@region id="html-upload" scope="global" chromeless="true" />
    <@region id="flash-upload" scope="global" chromeless="true" />
    <@region id="file-upload" scope="global" chromeless="true" />
    <@region id="dnd-upload" scope="global" chromeless="true"/>
    <@region id="share-header" scope="global"/>
    <#-- <@region id="title" scope="template"/> -->
</div>
<div id="doc-bd" class="doc-page">
    <@region id="document-header" scope="template"/>
    <#if hasPermission>
	    <@region id="actions-common" scope="template"/>
	    <@region id="actions" scope="template"/>
        <div class="yui-gc">
            <div class="bordered-panel doc-right-part">
                <@region id="document-actions" scope="template"/>
                <@region id="document-metadata" scope="template"/>
                <@region id="document-attachments" scope="template"/>
                <@region id="document-errands" scope="template"/>
                <@region id="document-workflows" scope="template"/>
                <@region id="document-connections" scope="template"/>
                <@region id="document-members" scope="template"/>
                <@region id="document-tags" scope="template"/>
                <@region id="document-history" scope="template"/>
                <@region id="document-forms" scope="template"/>
				<#-- Участие в ЮЗД -->
				<@region id="document-signed-docflow" scope="template"/>
            </div>
            <div id="main-content" class="main-content">
		        <div id="main-region">
                    <#if documentType?? && documentType == "{http://www.it.ru/logicECM/errands/1.0}document">
                        <@region id="errand-form" scope="template"/>
                    <#else>
                        <@region id="dashlet-panel" scope="template"/>
                    </#if>
	                <@region id="rating" scope="template"/>
	                <@region id="comments" scope="template"/>
	            </div>
	            <div id="custom-region" class="hidden1"></div>
		        <@region id="document-status-string" scope="template"/>
	        </div>
        </div>
    </#if>

    <@region id="html-upload" scope="template"/>
    <@region id="flash-upload" scope="template"/>
    <@region id="file-upload" scope="template"/>
	<@region id="dnd-upload" scope="template"/>
</div>
</@>

<@templateFooter>
<div id="alf-ft"></div>
</@>

<script type="text/javascript"> //<![CDATA[
(function() {
    var Event = YAHOO.util.Event;

    Event.on(window, "resize", function(e) {
        LogicECM.module.Base.Util.setDocPageHeight();
    }, this, true);

    Event.onDOMReady(function() {
        LogicECM.module.Base.Util.setDocPageHeight();
    });
})();
//]]></script>
