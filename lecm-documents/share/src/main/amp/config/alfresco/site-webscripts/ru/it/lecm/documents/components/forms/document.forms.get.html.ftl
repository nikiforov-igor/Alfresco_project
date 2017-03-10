<#assign el=args.htmlid/>

<div class="widget-bordered-panel forms-panel">
    <div id="${el}-wide-view" class="document-components-panel">
        <h2 id="${el}-heading" class="not-active">
        ${msg("heading")}
        </h2>

        <div id="${el}-formContainer">
            <#if reportsDescriptors?? && (reportsDescriptors?size > 0)>
                <ul id="document-reports-set" class="document-reports-set document-right-set">
                    <#assign i=0/>
                    <#escape x as x?js_string>
                        <#list reportsDescriptors as report>
                            <li class="text-broken">
                                <a href="#" class="theme-color-1"
                                   onClick='LogicECM.module.Documents.Reports.reportLinkClicked(this, {"reportCode": "${report.code}", "nodeRef":"${nodeRef}"});'><#if report.name != "">${report.name}<#else>
                                    (no name)</#if></a>
                            </li>
                        </#list>
                    </#escape>
                </ul>
            </#if>
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
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-documents/documents-reports.js',
            'components/form/date-range.js',
            'components/form/number-range.js'
        ], [
            'css/components/document-forms.css'
        ], create);
    }

    function create() {
        var formContainerChildren = Dom.getChildren("${el}-formContainer");
        if (formContainerChildren != null && formContainerChildren.length > 0) {
            var forms = Dom.getChildren(formContainerChildren[0]);

            if (forms.length > 0) {
                var heading = Dom.get("${el}-heading");

                Dom.removeClass(heading, "not-active");
                Alfresco.util.createTwister(heading, "DocumentForms");
            }
        }
    }

    Event.onDOMReady(init);

    LogicECM.services = LogicECM.services || {};
    if (LogicECM.services.documentViewPreferences) {
        var shortView = LogicECM.services.documentViewPreferences.getShowRightPartShort();
        if (shortView) {
            Dom.addClass("${el}-wide-view", "hidden");
            Dom.removeClass("${el}-short-view", "hidden");
        }

        YAHOO.Bubbling.on("showRightPartShortChanged", function () {
            var rightPartWide = Dom.get("${el}-wide-view");
            var rightPartShort = Dom.get("${el}-short-view");
            var shortView = LogicECM.services.documentViewPreferences.getShowRightPartShort();
            if (shortView) {
                Dom.addClass(rightPartWide, "hidden");
                Dom.removeClass(rightPartShort, "hidden");
            } else {
                Dom.addClass(rightPartShort, "hidden");
                Dom.removeClass(rightPartWide, "hidden");
            }
        });
    }
})();
//]]></script>