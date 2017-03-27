<!-- Comments List -->

<#if hasViewCommentPerm!false>
    <#assign el=args.htmlid?js_string>
    <#assign listEl=args.htmlid?js_string + "-list">

<div class="widget-bordered-panel comments-panel">
    <div id="${el}-wide-view" class="document-metadata-header document-components-panel">
        <h2 id="${el}-heading" class="dark alfresco-twister">
        ${msg("header.comments")}
        <span class="alfresco-twister-actions">
            <a id="${el}-action-expand" href="javascript:void(0);" class="expand comments-expand"
               title="${msg("label.expand")}">&nbsp</a>
        </span>
        </h2>
    </div>

    <div id="${el}-short-view" class="document-components-panel short-view hidden">
        <span class="alfresco-twister-actions">
            <a href="javascript:void(0);" class="expand comments-expand"
               title="${msg("label.expand")}">&nbsp</a>
        </span>
        <div class="right-block-content">
            <span class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="${msg('header.comments')}"></button>
               </span>
            </span>
        </div>
    </div>
</div>
<script type="text/javascript">//<![CDATA[
(function () {
    function init() {
        LogicECM.module.Base.Util.loadResources([
                'scripts/components/document-comments-list.js',
                'scripts/components/document-comments.js'
            ], [
                'components/comments/comments-list.css',
                'css/components/document-comments.css'
            ], createControl);
    }

    function createControl() {
        if (typeof LogicECM == "undefined" || !LogicECM) {
            LogicECM = {};
        }
        if (typeof LogicECM.DocumentCommentsComponent == "undefined" || !LogicECM.DocumentCommentsComponent) {
            LogicECM.DocumentCommentsComponent = {};
        }

        LogicECM.DocumentCommentsComponent = new LogicECM.DocumentComments("${el}").setOptions({
            nodeRef: "${nodeRef?js_string}",
            site: <#if site??>"${site?js_string}"<#else>null</#if>,
            activityType: <#if activityType??>"${activityType}"<#else>null</#if>,
            title: "${msg('header.comments')}"
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

</#if>