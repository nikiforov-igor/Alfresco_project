<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign params = field.control.params/>
<#assign height = params.height ! ""/>
<#assign viewerHeight = params.viewerHeight ! ""/>

<div class="form-field document-preview-cntrol">
	<script type="text/javascript">//<![CDATA[
	(function() {
		function init() {
            LogicECM.module.Base.Util.loadScripts([
                'scripts/lecm-documents/lecm-document-preview-control.js',
	            'components/preview/web-preview.js',
	            'components/preview/WebPreviewer.js',
	            'js/flash/extMouseWheel.js',
	            'components/preview/StrobeMediaPlayback.js',
	            'components/preview/Video.js',
	            'components/preview/Audio.js',
	            'components/preview/Flash.js',
	            'components/preview/Image.js',
	            'extras/components/preview/web-preview-extend.js',
	            'extras/components/preview/PdfJs.js',
	            'extras/components/preview/Embed.js',
	            'extras/components/preview/pdfjs/compatibility.js',
	            'extras/components/preview/pdfjs/pdf.js',
	            'extras/components/preview/spin.js'
		    ], createControl);
	    }
		function createControl() {
			var control = new LogicECM.module.Documents.DocumentPreviewControl("${fieldHtmlId}").setMessages(${messages});
			control.setOptions({
				taskId: "${form.arguments.itemId}",
				height: '${height}',
				viewerHeight: '${viewerHeight}'
			});
		}

		YAHOO.util.Event.onDOMReady(init);
	})();
	//]]></script>

	<div class="preview-select document-preview">
		<select id="${fieldHtmlId}-attachment-select"></select>
	</div>
	<div id="${fieldHtmlId}-preview-container" class="document-preview"></div>

	<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
</div>
