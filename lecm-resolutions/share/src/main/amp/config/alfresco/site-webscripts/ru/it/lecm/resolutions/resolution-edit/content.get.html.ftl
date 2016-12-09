<#assign el=args.htmlid?html>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/documents/edit/lecm-document-edit.js" group="document-edit"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-preview.js" group="document-edit"/>

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
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata.css" group="document-edit"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-resolution/resolution-form.css" group="document-edit"/>
</@>

<@markup id="widgets">
	<@createWidgets group="document-edit"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>
		<div class="container">
			<div id="${el}-body" class="document-metadata without-preview resolution-form"></div>
		</div>
	</@>
</@>