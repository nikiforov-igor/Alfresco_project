<@script type="text/javascript" src="${url.context}/res/scripts/components/document-history.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-history.css" />

<!-- Parameters and libs -->
<#assign el=args.htmlid/>
<#if hasViewHistoryPerm!false>
<!-- Markup -->
<div class="widget-bordered-panel history-panel">
<div id = "${el}-wide-view" class="document-components-panel">
    <h2 id="${el}-heading" class="dark alfresco-twister">
        ${msg("heading")}
        <span class="alfresco-twister-actions">
            <a href="javascript:void(0);" onclick="" class="expand history-expand" title="${msg("label.expand")}">&nbsp</a>
         </span>
    </h2>

    <div id="${el}-formContainer">
        <div id="${el}-form" class="hidden1"></div>
    </div>
    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            new LogicECM.DocumentHistory("${el}").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        title: "${msg('heading')}"
                    }).setMessages(${messages});
        }

            YAHOO.util.Event.onDOMReady(init);
        })();
        //]]>
        </script>
    </div>

    <div id="${el}-short-view" class="document-components-panel short-view hidden">
        <span class="alfresco-twister-actions">
            <a href="javascript:void(0);" onclick="" class="expand history-expand" title="${msg("label.expand")}">&nbsp</a>
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
LogicECM.services = LogicECM.services || {};
if (LogicECM.services.DocumentViewPreferences) {
    var shortView = LogicECM.services.DocumentViewPreferences.getShowRightPartShort();
    if (shortView) {
        Dom.addClass("${el}-wide-view", "hidden");
        Dom.removeClass("${el}-short-view", "hidden");
    }
}
//]]></script>
</div>
</#if>