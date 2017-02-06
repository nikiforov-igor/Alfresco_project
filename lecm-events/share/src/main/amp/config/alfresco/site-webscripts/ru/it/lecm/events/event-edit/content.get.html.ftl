<#assign el=args.htmlid?html>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/documents/edit/lecm-document-edit.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-preview.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/lecm-event-edit.js" group="document-edit"/>

	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-dnd-uploader.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-dnd-uploader-control.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-uploader-initializer.js" group="document-edit"/>

	<@script type="text/javascript" src="${url.context}/res/components/preview/web-preview.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/components/preview/WebPreviewer.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/js/flash/extMouseWheel.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/components/preview/StrobeMediaPlayback.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/components/preview/Video.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/components/preview/Audio.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/components/preview/Flash.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/components/preview/Image.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/extras/components/preview/web-preview-extend.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/extras/components/preview/PdfJs.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/extras/components/preview/Embed.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/extras/components/preview/pdfjs/compatibility.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/extras/components/preview/pdfjs/pdf.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/extras/components/preview/spin.js" group="document-edit"/>
</@>

<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata.css" group="document-create"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-events/event-create-set.css" group="document-create"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/lecm-dnd-uploader-control-with-preview.css" group="document-create"/>
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
									<li class="event-cancel"><a id="${el}-event-action-cancel" href="#">${msg("button.cancel")}</a></li>
								</ul>
							</div>
						</div>
						<div class="event-create-attachments">
							<h2 class="alfresco-twister alfresco-twister-open">
								${msg("label.events.attachments")}
							</h2>
							<div class="control dnd-uploader dnd-uploader-with-preview editmode">
								<div id="${el}-uploader-block" class="uploader-block">
									<fieldset>
										<legend>${msg("label.add-file")}</legend>
										<img id="${el}-uploader-button" src="${url.context}/res/images/lecm-base/components/plus.png" alt="" class="uploader-button"> <br/>
										<span class="drag-tip">${msg("label.drag-file")}</span>
									</fieldset>
								</div>
								<div class="container">
									<div class="buttons-div"></div>
									<div class="value-div">
										<ul id="${el}-attachments" class="attachments-list"></ul>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div id="${el}-body" class="event-create-center">
					</div>
				</div>
			</div>
		</div>

		<#assign  updateRepeatedFormId = el + "-update-repeated-form"/>
		<div id="${updateRepeatedFormId}" class="yui-panel hidden1">
			<div id="${updateRepeatedFormId}-head" class="hd">${msg("logicecm.view")}</div>
			<div id="${updateRepeatedFormId}-body" class="bd">
				<div id="${updateRepeatedFormId}-content" class="update-repeated">
					<input type="radio" name="events-update-repeated" value="THIS" checked> ${msg("label.events.update.repeated.this")}<br/>
					<input type="radio" name="events-update-repeated" value="ALL"> ${msg("label.events.update.repeated.all")}<br/>
					<input type="radio" name="events-update-repeated" value="ALL_NEXT"> ${msg("label.events.update.repeated.all_nex")}<br/>
					<input type="radio" name="events-update-repeated" value="ALL_PREV"> ${msg("label.events.update.repeated.all_prev")}<br/>
				</div>
				<div class="bdft">
                   <span id="${updateRepeatedFormId}-ok" class="yui-button yui-push-button">
                       <span class="first-child">
                           <button type="button" tabindex="0">${msg("button.ok")}</button>
                       </span>
                   </span>
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