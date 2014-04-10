<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader>
	<@script type="text/javascript" src="${page.url.context}/res/components/documentlibrary/actions.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/yui/resize/resize.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/res/components/document-details/document-metadata.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/dashlets/lecm-errands-dashlet.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-documents/lecm-document-errands.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-documents/lecm-document-ajax-content.js"></@script>
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/documentlibrary/actions.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/document-details/document-metadata.css" />

	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-documents/graph-tree-control.js"></@script>
	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-documents/graph-tree.js"></@script>
	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-documents/lecm-document-regnum-uniqueness-validator.js" />

    <#-- IMPORTANT for IE ! -->
    <#-- В IE9 следующие файлы подключаются только при использовании тэга <link>,
        при подключении макросом - не работают.
        Причина не выявлена, возможно, как-то связано с местом расположения файлов (/css/...)
    -->
    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-documents/yahoo-datatable.css"/>
    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-documents/graph-view-control.css"/>
    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/light-blue-bgr.css" />
    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/document-components-panel.css" />
    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/dashlet-components.css" />
    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/page-document.css" />

    <#if documentType?? && documentType == "{http://www.it.ru/logicECM/errands/1.0}document">
        <link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-errands/errands-metadata.css" />
    </#if>
    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-errands/errands-form.css" />

    <@templateHtmlEditorAssets />
    <script type="text/javascript">
        //<![CDATA[
        var currentExtendedComponent = null;


        LogicECM.module = LogicECM.module || {};
        LogicECM.module.Documents = LogicECM.module.Documents|| {};
        LogicECM.module.Documents.ERRANDS_SETTINGS =
            <#if errandsSettings?? >
            ${errandsSettings}
            <#else>
            {}
            </#if>;
        //]]>
    </script>
</@>

<@templateBody>
<div id="alf-hd">
    <@region id="header" scope="global"/>
    <@region id="title" scope="template"/>
</div>
<div id="doc-bd">
    <@region id="document-header" scope="template"/>
    <#if hasPermission>
	    <@region id="actions-common" scope="template"/>
	    <@region id="actions" scope="template"/>
        <div class="yui-gc">
	        <div id="main-content" class="yui-u first">
		        <div id="main-region">
                    <#if documentType?? && documentType == "{http://www.it.ru/logicECM/errands/1.0}document">
                        <@region id="errand-form" scope="template"/>
                    <#else>
                        <@region id="dashlet-panel" scope="template"/>
                    </#if>
	                <@region id="rating" scope="template"/>
	                <@region id="comments" scope="template"/>
	            </div>
	            <div id="custom-region" style="display:none"></div>
		        <@region id="document-status-string" scope="template"/>
	        </div>
            <div class="yui-u bordered-panel doc-right-part">
	            <@region id="document-final-actions" scope="template"/>
                <@region id="document-actions" scope="template"/>
                <@region id="document-metadata" scope="template"/>
                <@region id="document-attachments" scope="template"/>
                <@region id="document-tasks" scope="template"/>
                <@region id="document-workflows" scope="template"/>
                <@region id="document-connections" scope="template"/>
                <@region id="document-members" scope="template"/>
                <@region id="document-tags" scope="template"/>
                <@region id="document-history" scope="template"/>
                <@region id="document-forms" scope="template"/>
				<#-- Участие в ЮЗД -->
				<@region id="document-signed-docflow" scope="template"/>
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
