<#assign id=args.htmlid/>

<#if hasPermission>
<div class="widget-bordered-panel workflows-panel">
    <div id="${id}-wide-view" class="document-metadata-header document-components-panel">
        <h2 id="${id}-heading" class="dark">
        ${msg("heading")}
            <span class="alfresco-twister-actions">
                    <div>
                        <a id="${id}-action-expand" href="javascript:void(0);" class="expand workflows-expand"
                           title="${msg("label.expand")}">&nbsp</a>
                    </div>
                </span>
        </h2>

        <div id="${id}-formContainer">
            <div class="right-workflows-container" id="${id}-results"></div>

            <span id="${id}-right-more-link-container" class="hidden1">
                    <div class="right-more-link-arrow" onclick="documentWorkflowsComponent.onExpand();"></div>
                    <div class="right-more-link"
                         onclick="documentWorkflowsComponent.onExpand();">${msg('right.label.more')}</div>
                    <div class="clear"></div>
                </span>
        </div>
    </div>

    <div id="${id}-short-view" class="document-components-panel short-view hidden">
        <span class="alfresco-twister-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" class="expand workflows-expand" title="${msg("label.expand")}">&nbsp</a>
        </span>
        <div class="right-block-content">
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
                'scripts/components/document-workflows.js'
            ], [
                'css/components/document-workflows.css'
        ], create);
    }

    function create() {
        Alfresco.util.createTwister("${id}-heading", "DocumentWorkflows");

        new LogicECM.DocumentWorkflows("${id}").setOptions({
                    nodeRef: "${nodeRef}",
                    title: "${msg('heading')}"
                }).setMessages(${messages});

        new LogicECM.module.Document.Ajax.Content("${id}-results").setOptions(
                {
                    contentURL: Alfresco.constants.URL_PAGECONTEXT + "lecm/components/document/document-workflows/content",
                    requestParams: {
                        nodeRef: "${nodeRef}",
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
</#if>