<#assign el=args.htmlid?html>

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
			<div class="event-create">
				<div id="${el}_create-event-set" class="create-event-set">
					<div class="event-create-right">
						<div class="event-create-actions">
							<h2 class="alfresco-twister alfresco-twister-open">
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
						<div id="${el}-body"></div>
						<div><h2>${msg("label.meeting.holding.protocol")}</h2></div>
						<div id="${el}-items"></div>
						<div>
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

		<script type="text/javascript">//<![CDATA[
		(function() {
			var Dom = YAHOO.util.Dom,
					Event = YAHOO.util.Event,
					Selector = YAHOO.util.Selector;
			var setId = "${el}_create-event-set";

			function init() {
				var expandedClass = "alfresco-twister-open",
						collapsedClass = "alfresco-twister-closed";
				var h2s = Selector.query(".event-create-right h2", setId);

				if (h2s && h2s.length > 0) {
					Event.addListener(h2s, "click", function() {
						var el = this;

						if (Dom.hasClass(el, collapsedClass)) {
							Dom.replaceClass(el, collapsedClass, expandedClass);
						} else {
							Dom.replaceClass(el, expandedClass, collapsedClass);
						}
					});
				}
			}

			Event.onDOMReady(init);
		}) ();
		//]]></script>
	</@>
</@>