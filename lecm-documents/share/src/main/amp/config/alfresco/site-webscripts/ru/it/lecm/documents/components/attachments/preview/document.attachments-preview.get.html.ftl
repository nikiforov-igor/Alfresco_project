<#assign aDateTime = .now>
<#assign el=args.htmlid + aDateTime?iso_utc/>
<div id="${el}" class="document-preview">
    <div class="doc-preview-container">
        <div class="panel-header preview-toolbar">
            <div class="attachments-actions">
                <div>
                    <select id="${el}-attachment-select" class="attachments-select"></select>
                </div>
                <div id="${el}-versions-actions" class="attachment-actions hidden">
                    <input id="${el}-versions-actions-button" type="button">
                </div>
                <div id="${el}-attachment-actions" class="preview-actions hidden1">
                    <input id="${el}-attachment-actions-button" type="button"></input>
                </div>
                <div id="${el}-attachment-add-container" class="preview-upload hidden1">
                    <div id="${el}-attachment-add"></div>
                </div>
            </div>
            <div class="lecm-dashlet-actions">
                <a id="${el}-action-collapse" class="collapse" title="${msg("btn.collapse")}"></a>
                <a id="${el}-show-list" class="preview-show-list" title="${msg("btn.show-attachment-list")}"></a>
            </div>
        </div>
        <div id="${el}-preview-container" class="document-preview body"></div>
    </div>
</div>
<div class="clear"></div>
<script type="text/javascript">//<![CDATA[
(function () {
    new LogicECM.DocumentAttachmentsPreview("${el}").setOptions({
        nodeRef: "${nodeRef}",
        inclBaseDoc: ${inclBaseDoc?string("true", "false")}
    }).setMessages(${messages});

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
                    'css/components/document-preview-control.css',
                    'css/components/document-attachments-preview.css',
                    'extras/components/preview/viewer.css'
                ], createControl);
    }

    function createControl() {
        var control = new LogicECM.module.Documents.DocumentPreviewControl("${el}").setMessages(${messages});
        control.setOptions({
            resizeable: true,
            itemId: "${nodeRef}",
            forTask: false
        });
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>