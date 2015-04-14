<#assign el=args.htmlid?html>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/documents/edit/lecm-document-edit.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-preview.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/lecm-event-edit.js" group="document-edit"/>

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
</@>

<@markup id="widgets">
	<@createWidgets group="document-edit"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>
		<div class="container">
			<div id="${el}-body" class="document-metadata"></div>
			<div id="${el}-preview" class="document-preview body"></div>
		</div>

		<#assign  updateRepeatedFormId = el + "-update-repeated-form"/>
		<div id="${updateRepeatedFormId}" class="yui-panel hidden1">
			<div id="${updateRepeatedFormId}-head" class="hd">${msg("logicecm.view")}</div>
			<div id="${updateRepeatedFormId}-body" class="bd">
				<div id="${updateRepeatedFormId}-content">
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
	</@>
</@>