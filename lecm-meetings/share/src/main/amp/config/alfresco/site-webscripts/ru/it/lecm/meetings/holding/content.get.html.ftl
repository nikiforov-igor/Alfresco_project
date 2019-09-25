<#assign el=args.htmlid?html>
<script type="text/javascript">
    if (typeof LogicECM == "undefined" || !LogicECM) {
        LogicECM = {};
    }
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.Documents = LogicECM.module.Documents || {};
    LogicECM.module.Documents.isEditLockEnabled = ${isEditLockEnabled?c};
</script>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/documents/edit/lecm-document-edit.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-meetings/lecm-meeting-holding.js" group="document-edit"/>
</@>

<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata.css" group="document-create"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-events/event-create-set.css" group="document-create"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-meetings/meeting-holding.css" group="document-create"/>
</@>

<@markup id="widgets">
	<@createWidgets group="document-edit"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>
		<div class="container">
			<div class="event-create meeting">
				<div id="${el}_create-event-set" class="create-event-set">
					<div class="event-create-right">
						<div class="event-create-actions">
							<h2>
							${msg("label.events.actions.onCreate")}
							</h2>
							<div>
								<ul>
									<li class="event-save"><a id="${el}-event-action-save" href="#">${msg("label.save")}</a></li>
									<li class="event-save"><a id="${el}-event-action-finish" href="#">${msg("button.meeting.finish")}</a></li>
								</ul>
							</div>
						</div>
					</div>
					<div class="event-create-center">
						<div id="${el}-body" class="meeting-holding-main"></div>
						<div class="title">${msg("label.meeting.holding.protocol")}</div>
						<div id="${el}-items" class="meeting-holding-items"></div>
						<div class="meeting-holding-buttons">
							<span id="${el}-create-new-item-button" class="yui-button yui-push-button search-icon">
								<span class="first-child">
									<button type="button">${msg('label.button.meeting.holding.create.item')}</button>
								</span>
							</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</@>
</@>