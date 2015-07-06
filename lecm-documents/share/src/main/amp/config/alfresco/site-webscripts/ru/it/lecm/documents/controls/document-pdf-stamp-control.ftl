<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign params = field.control.params/>


<#if params.datasource??>
    <#assign datasource = params.datasource>
<#else>
    <#assign datasource = "/lecm/documents/stamp/default">
</#if>

<#if params.code??>
	<#assign code = params.code>
<#else>
	<#assign code = "DEFAULT">
</#if>


<div class="control document-preview ${params.cssClass!''}">
	<script type="text/javascript">//<![CDATA[
	(function() {
		function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-documents/lecm-document-pdf-stamp-control.js',
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
                'css/components/document-preview-control.css',
                'css/lecm-documents/document-pdf-stamp.css'
            ], createControl);
	    }

		function createControl() {
			var control = new LogicECM.module.Documents.PdfMarkupControl("${fieldHtmlId}").setMessages(${messages});
			control.setOptions({
				itemId: "${form.arguments.itemId}",
				datasource: "${datasource}",
				code: "${code}"
			});
		}
		
		YAHOO.util.Event.onDOMReady(init);
	})();
	//]]></script>

    <div class="doc-preview-container">
        <div id="${fieldHtmlId}-error" class="hidden1">Не удалось загрузить документ</div>
        <div id="${fieldHtmlId}-preview-container" class="document-preview body"></div> <#-- не удалять! класс 'body' важен для расчета высоты области просмотра -->

        <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
    </div>
</div>
<div class="clear"></div>