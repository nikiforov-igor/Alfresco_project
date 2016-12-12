<#assign el=args.htmlid?html>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/documents/create/lecm-document-create.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-preview.js" group="document-create"/>

	<@script type="text/javascript" src="${url.context}/res/components/preview/web-preview.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/components/preview/WebPreviewer.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/js/flash/extMouseWheel.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/components/preview/StrobeMediaPlayback.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/components/preview/Video.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/components/preview/Audio.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/components/preview/Flash.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/components/preview/Image.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/extras/components/preview/web-preview-extend.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/extras/components/preview/PdfJs.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/extras/components/preview/Embed.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/extras/components/preview/pdfjs/compatibility.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/extras/components/preview/pdfjs/pdf.js" group="document-create"/>
	<@script type="text/javascript" src="${url.context}/res/extras/components/preview/spin.js" group="document-create"/>
</@>

<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata.css" group="document-create"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-resolution/resolution-form.css" group="document-create"/>
</@>

<@markup id="widgets">
	<@createWidgets group="document-create"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>
		<#if hasPermission>
			<div class="container">
				<div id="${el}-body" class="document-metadata without-preview resolution-form"></div>
			</div>
		<#else>
			<div class="document-header">
				<div class="status-banner">
					${msg(accessMsg)}
				</div>
			</div>
		</#if>
	</@>
</@>