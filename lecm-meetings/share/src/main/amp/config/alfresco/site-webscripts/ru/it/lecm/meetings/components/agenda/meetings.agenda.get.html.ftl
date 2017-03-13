<!-- Parameters and libs -->
<#assign el=args.htmlid/>
<#if agendaInfo?? >
<!-- Markup -->
<div class="widget-bordered-panel meeting-agenda-panel">
    <div id="${el}-wide-view" class="document-metadata-header document-components-panel">
        <h2 id="${el}-heading" class="dark">
        ${msg("heading")}
            <span class="alfresco-twister-actions">
	            <a id="${el}-action-expand" href="javascript:void(0);" class="expand agenda-expand"
                   title="${msg("label.expand")}">&nbsp</a>
	        </span>
        </h2>

        <div id="${el}-formContainer" class="agenda-set right-block-content">
            <#if agendaInfo?? >
                <div><span>${msg("title.agenda_size")}: ${agendaInfo.size}</span></div>
                <#if !agendaInfo.hideStatus>
                    <div><span>${msg("agenda_status."+agendaInfo.status)}</span></div>
                </#if>
            <#else>
                <div class="block-empty-body">
				    <span class="block-empty faded">
                    ${msg("message.block.empty")}
				    </span>
                </div>
            </#if>
        </div>
    </div>
    <div id="${el}-short-view" class="document-components-panel short-view hidden">
          <span class="alfresco-twister-actions">
            <a href="javascript:void(0);" class="expand agenda-expand"
               title="${msg("label.expand")}">&nbsp</a>
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
    if (typeof LogicECM == "undefined" || !LogicECM) {
        LogicECM = {};
    }
    if (typeof LogicECM.MeetingAgendaInfoComponent == "undefined" || !LogicECM.MeetingAgendaInfoComponent) {
        LogicECM.MeetingAgendaInfoComponent = {};
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/advsearch.js',
            'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-meetings/components/meetings-agenda-info.js'
        ], [
            'css/lecm-meetings/meeting-agenda.css'
        ], create);
    }

    function create() {
        LogicECM.MeetingAgendaInfoComponent = new LogicECM.MeetingAgenda("${el}").setOptions(
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
//]]>
</script>

</#if>