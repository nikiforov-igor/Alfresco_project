<@markup id="css" >
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-attachments.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-attachments-list.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-attachments-list-actions.css" />
</@>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
	
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-meetings/components/meetings-agenda-info.js"></@script>
</@>

<@markup id="html">
	<!-- Parameters and libs -->
	<#assign el=args.htmlid/>
	<#if agendaInfo?? >
	<!-- Markup -->
	<div class="widget-bordered-panel">
	<div class="document-metadata-header document-components-panel">
	    <h2 id="${el}-heading" class="dark">
	        ${msg("heading")}
	        <span class="alfresco-twister-actions">
	            <a id="${el}-action-expand" href="javascript:void(0);" class="expand agenda-expand" title="${msg("label.expand")}">&nbsp</a>
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
	    <script type="text/javascript">//<![CDATA[

		if (typeof LogicECM == "undefined" || !LogicECM) {
		    LogicECM = {};
		}
    	if (typeof LogicECM.MeetingAgendaInfoComponent == "undefined" || !LogicECM.MeetingAgendaInfoComponent) {
		    LogicECM.MeetingAgendaInfoComponent = {};
		}

	    (function () {
	        function init() {
	            LogicECM.MeetingAgendaInfoComponent = new LogicECM.MeetingAgenda("${el}").setOptions(
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
	</div>
	</#if>
</@>