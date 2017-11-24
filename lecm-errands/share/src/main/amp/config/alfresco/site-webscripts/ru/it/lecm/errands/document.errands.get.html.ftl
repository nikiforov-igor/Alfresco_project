<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/lecm-document-errands.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/document-errands.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/lecm-errands-dashlet.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/lecm-document-errands.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/errands-links.js"></@script>

<!-- Document Metadata Header -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-metadata.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-form.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-main-form.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/document-errands.css" />

<#assign id=args.htmlid/>

<div class="widget-bordered-panel errands-panel">
    <div id="${id}-wide-view">
        <div class="document-metadata-header document-components-panel" id="${id}-results">
            <h2 id="${id}-heading" class="dark">
            ${msg("heading")}
                <span class="alfresco-twister-actions">
                    <a id="${id}-action-expand" href="#" onclick="return false;" class="expand errands-expand"
                       title="${msg("label.expand")}">&nbsp;</a>
                </span>
            </h2>

            <div id="${id}-formContainer"></div>
        </div>
    </div>

    <div id="${id}-short-view" class="document-components-panel short-view hidden">
        <span class="alfresco-twister-actions">
            <a href="#" onclick="return false;" class="expand errands-expand"
               title="${msg("label.expand")}">&nbsp</a>
        </span>
        <div id="${id}-formContainer" class="right-block-content">
            <span id="${id}-errands-icon" class="yui-button yui-push-button">
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
        LogicECM.module.Base.Util.loadResources(['scripts/lecm-errands/lecm-document-errands.js'],
                [], create);
    }

    function create() {
        new LogicECM.module.Document.Ajax.Content("${id}-results").setOptions(
        {
            contentURL: Alfresco.constants.URL_PAGECONTEXT + "lecm/components/document/document-errands/content",
            requestParams: {
                nodeRef: "${args.nodeRef}",
                state: "all",
                errandsLimit: 5,
                containerHtmlId: "${id}"
            },
            containerId: "${id}-results"
        }).setMessages(${messages});
    }

    YAHOO.util.Event.onDOMReady(init);

    LogicECM.services = LogicECM.services || {};
    if (LogicECM.services.documentViewPreferences) {
        var shortView = LogicECM.services.documentViewPreferences.getShowRightPartShort();
        if (shortView) {
            Dom.addClass("${id}-wide-view", "hidden");
            Dom.removeClass("${id}-short-view", "hidden");
        }
    }
})();
//]]></script>