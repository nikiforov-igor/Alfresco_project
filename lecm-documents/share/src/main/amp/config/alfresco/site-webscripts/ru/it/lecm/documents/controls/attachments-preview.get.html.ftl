<#assign formId=args.htmlid?js_string?html>
<html>


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
                    var control = new LogicECM.module.Documents.DocumentPreviewControl("${formId}").setMessages(${messages});
			control.setOptions({
				itemId: "${nodeRef}",
				forTask: ${forTask?string},
				baseDocAssocName: "${baseDocAssocName!""}",
                selectedAttachmentNodeRef: "${selectedAttachmentNodeRef}"
			});
            control.onReady();
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

        function redirectToAttachmentPage() {
            window.location = Alfresco.constants.URL_PAGECONTEXT + "document-attachment?nodeRef=" + document.getElementById("modalWindow-attachment-select").value;
        }
	//]]></script>

    <form class="bd">
        <div class="form-fields">

            <div>
                <div class="label-div">
                    <select id="modalWindow-attachment-select"></select>
                </div>
                <div class="container">
                    <div id="${formId}-actions" class="actions">
                    </div>
                    <div class="value-div add-actions">
                        <div onClick="redirectToAttachmentPage()" class="list" title="${msg("attach.preview.openList")}"></div>
                </div>
            </div>
        </div>

            <div id="${formId}-preview-container" class="document-preview body"></div> <#-- не удалять! класс 'body' важен для расчета высоты области просмотра -->
        </div>
        <div align="center" id="${formId}-buttons" class="form-buttons">
            <button type="button" tabindex="104"  id="${formId}-cancel" name="-">Закрыть</button>
        </div>
    </form>

</div>

<div class="clear"></div>

</html>
