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
			var control = new LogicECM.module.Documents.DocumentPreviewControl("modalWindow").setMessages(${messages});
			control.setOptions({
				itemId: "${nodeRef}",
				forTask: ${forTask?string},
				additionalType: "${additionalType!""}",
				additionalAssoc: "${additionalAssoc!""}",
                selectedAttachmentNodeRef: "${selectedAttachmentNodeRef}"
			});
            control.onReady();
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
                    <div class="value-div" align="right">
                        <a href="javascript:void(0)" onClick="redirectToAttachmentPage()">Открыть страницу вложений</a>
                    </div>
                </div>
            </div>

            <div id="modalWindow-preview-container" class="document-preview body"></div> <#-- не удалять! класс 'body' важен для расчета высоты области просмотра -->
        </div>
        <div align="center" id="modalWindow-buttons" class="form-buttons">
            <button type="button" tabindex="104"  id="modalWindow-cancel" name="-">Закрыть</button>
        </div>
    </form>

</div>

<div class="clear"></div>

</html>
