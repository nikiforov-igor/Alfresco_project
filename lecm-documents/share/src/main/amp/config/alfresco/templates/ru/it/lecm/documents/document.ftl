<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader>
	<@script type="text/javascript" src="${url.context}/res/components/documentlibrary/actions.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/yui/resize/resize.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/components/document-details/document-metadata.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/graph-tree-control.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/graph-tree.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/lecm-document-ajax-content.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/lecm-document-save-last.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/components/document-view-preferences.js"></@script>

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

    <@templateHtmlEditorAssets />
	</@>

    <#if documentType??>
    <script type="text/javascript">//<![CDATA[
            LogicECM.services = LogicECM.services || {};
            LogicECM.services.DocumentViewPreferences = new LogicECM.module.DocumentViewPreferences("${page.url.args.nodeRef}", "${documentType}", "${defaultExpandComponent}");
    //]]></script>
    </#if>

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
            <div id="doc-right-part" class="bordered-panel doc-right-part">
                <#if documentType??>
                <script type="text/javascript">//<![CDATA[
                    var SHOW_RIGHT_PART_SHORT = LogicECM.services.DocumentViewPreferences.getShowRightPartShort();
                    if (SHOW_RIGHT_PART_SHORT) {
                        document.getElementById("doc-right-part").className += "-short";
                    }
                    YAHOO.Bubbling.on("showRightPartShortChanged", function () {
                        var SHOW_RIGHT_PART_SHORT = LogicECM.services.DocumentViewPreferences.getShowRightPartShort();
                        var rightPart = Dom.get("doc-right-part");

                        if (SHOW_RIGHT_PART_SHORT) {
                            Dom.removeClass(rightPart, "doc-right-part");
                            Dom.addClass(rightPart, "doc-right-part-short");
                        } else {
                            Dom.removeClass(rightPart, "doc-right-part-short");
                            Dom.addClass(rightPart, "doc-right-part");
                        }

                    });
                //]]></script>
                </#if>
                <@region id="document-actions" scope="template"/>
                <@region id="base-doc-actions" scope="template"/>
                <@region id="document-metadata" scope="template"/>
                <@region id="document-attachments" scope="template"/>
                <@region id="document-workflows" scope="template"/>
                <@region id="document-connections" scope="template"/>
                <@region id="document-members" scope="template"/>
                <@region id="document-tags" scope="template"/>
                <@region id="document-history" scope="template"/>
                <@region id="document-forms" scope="template"/>
                <@region id="comments" scope="template"/>
				<#-- Участие в ЮЗД -->
				<@region id="document-signed-docflow" scope="template"/>
            </div>
            <div id="main-content" class="main-content">
                <div id="custom-region" class="hidden1"></div>
		        <div id="main-region">
                    <@region id="dashlet-panel" scope="template"/>
	                <@region id="rating" scope="template"/>
	            </div>
	            <div id="custom-region" class="hidden1"></div>
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
