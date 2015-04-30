<@markup id="css" >
<#-- CSS Dependencies -->
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/taglibrary/taglibrary.css" group="calendar"/>
</@>

<@markup id="js">
<#-- JavaScript Dependencies -->
    <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="calendar"/>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/lecm-calendar-view.js" group="calendar"/>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/calendar-view-agenda.js" group="calendar"/>
</@>

<@markup id="widgets">
    <@createWidgets group="calendar"/>
    <@inlineScript group="calendar">
	Alfresco.util.addMessages(${messages}, "Alfresco.EventInfo");
    </@>
</@>

<@markup id="html">
    <@uniqueIdDiv>
        <#assign el=args.htmlid?html>
		<!-- agenda -->
		<div class="agenda-header">
            <h2 id="calTitle">&nbsp;</h2>
            <div><a href="#" id="${el}_expand_all">${msg("calendar.agenda.expand-all")}</a></div>
            <div><a href="#" id="${el}_collapse_all">${msg("calendar.agenda.collapse-all")}</a></div>
        </div>
		<div id="${el}Container" class="alf-calendar agendaview">
			<div id="${el}View">
				<div id="${el}View-noEvent" class="noEvent">
					<p id="${el}View-defaultText" class="instructionTitle">${msg("agenda.initial-text")}</p>
				</div>
			</div>
		</div>
    </@>
</@>