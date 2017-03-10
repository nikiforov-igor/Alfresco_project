
<!-- Parameters and libs -->
<#assign el=args.htmlid/>
<#if hasViewHistoryPerm!false>
<!-- Markup -->
<div class="widget-bordered-panel history-panel">
    <div id = "${el}-wide-view" class="document-components-panel">
        <h2 id="${el}-heading" class="dark alfresco-twister">
            ${msg("heading")}
            <span class="alfresco-twister-actions">
                <a href="javascript:void(0);" class="expand history-expand" title="${msg("label.expand")}">&nbsp</a>
             </span>
        </h2>

        <div id="${el}-formContainer">
            <div id="${el}-form" class="hidden1"></div>
        </div>
    </div>

    <div id="${el}-short-view" class="document-components-panel short-view hidden">
        <span class="alfresco-twister-actions">
            <a href="javascript:void(0);" class="expand history-expand" title="${msg("label.expand")}">&nbsp</a>
        </span>
        <div class="right-block-content">
            <span class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="${msg('heading')}"></button>
               </span>
            </span>
        </div>
    </div>
<script type="text/javascript">//<![CDATA[
(function() {
    function init() {
        LogicECM.module.Base.Util.loadResources([
                'scripts/components/document-history.js'
            ], [
                'css/components/document-history.css'
            ], create);
    }

    function create() {
        new LogicECM.DocumentHistory("${el}").setOptions(
                {
                    nodeRef: "${nodeRef}",
                    title: "${msg('heading')}"
                }).setMessages(${messages});
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
</div>
</#if>