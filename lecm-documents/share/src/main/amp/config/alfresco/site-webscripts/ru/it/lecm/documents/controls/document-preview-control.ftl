<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign params = field.control.params/>


<#if params.forTask?? && params.forTask == "false">
    <#assign forTask = false>
<#else>
    <#assign forTask = true>
</#if>

<div class="control document-preview ${params.cssClass!''}">
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
				'extras/components/preview/ResizeSensor.js',
				'extras/components/preview/ElementQueries.js',
                'extras/components/preview/l10n.js',
                'extras/components/preview/viewer.js',
	            'extras/components/preview/web-preview-extend.js',
	            'extras/components/preview/PdfJs.js',
	            'extras/components/preview/Embed.js',
	            'extras/components/preview/pdfjs/compatibility.js',
	            'extras/components/preview/pdfjs/pdf.js',
	            'extras/components/preview/spin.js'
            ],
            [
                'extras/components/preview/viewer.css',
                'css/components/document-preview-control.css'
            ], createControl);
	    }
		function createControl() {
			loadExternalResourceBundle();
			var control = new LogicECM.module.Documents.DocumentPreviewControl("${fieldHtmlId}").setMessages(${messages});
			control.setOptions({
				itemId: "${form.arguments.itemId}",
				forTask: ${forTask?string}
			});
		}
		
        function loadExternalResourceBundle() {
            var resourceRef = document.createElement('link');
            resourceRef.setAttribute('rel', 'resource');
            resourceRef.setAttribute('type', 'application/l10n');
            resourceRef.setAttribute('href', Alfresco.constants.URL_RESCONTEXT + 'extras/components/preview/locale/locale.properties');
            document.getElementsByTagName("head")[0].appendChild(resourceRef);
        }

		YAHOO.util.Event.onDOMReady(init);
	})();
	//]]></script>

    <div class="doc-preview-container">
        <div id="${fieldHtmlId}-attachment-select-container" class="preview-select hidden1">
            <select id="${fieldHtmlId}-attachment-select"></select>
        </div>
	    <div id="${fieldHtmlId}-attachment-select-empty-container" class="preview-select-empty hidden1">
		    <h2>${msg("message.preview.attachments.empty")}</h2>
	    </div>
        <div id="${fieldHtmlId}-preview-container" class="document-preview body"></div> <#-- не удалять! класс 'body' важен для расчета высоты области просмотра -->

        <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
    </div>
</div>
<div class="clear"></div>