<script type="text/javascript">//<![CDATA[
(function()
{
    function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-documents/lecm-document-pdf-stamp-size-control.js'
        ], createControl);
    }
    function createControl(){
        var control = new LogicECM.module.Documents.StampSizeControl("${fieldHtmlId}").setMessages(${messages});
        control.setOptions({
            htmlid: "${args.htmlid?html}"
        });
    }

    YAHOO.util.Event.onDOMReady(init);

})();
//]]></script>
<div class="control textfield viewmode">
    <div class="container">
        <div id="${fieldHtmlId}" class="value-div">Размер изображения для просмотра <span id="${fieldHtmlId}-display"></span>, для печати (300dpi) <span id="${fieldHtmlId}-print"></span></div>
    </div>
</div>
<div class="clear"></div>
