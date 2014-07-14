<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign params = field.control.params/>

<div class="control document-preview">
	<script type="text/javascript">//<![CDATA[
	(function() {
		function init() {
            LogicECM.module.Base.Util.loadResources([
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
            ],
            [
                'css/components/document-preview-control.css'
            ], createControl);
	    }
		function createControl() {
			var control = new LogicECM.module.Documents.DocumentPreviewControl("${fieldHtmlId}").setMessages(${messages});
			control.setOptions({
				taskId: "${form.arguments.itemId}"
			});
		}
		
		YAHOO.util.Event.onDOMReady(init);
	})();
	//]]></script>

	<div class="preview-select">
		<select id="${fieldHtmlId}-attachment-select"></select>
	</div>
	<div id="${fieldHtmlId}-preview-container" class="document-preview body"></div> <#-- не удалять! класс 'body' важен для расчета высоты области просмотра -->

	<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
</div>
<div class="clear"></div>