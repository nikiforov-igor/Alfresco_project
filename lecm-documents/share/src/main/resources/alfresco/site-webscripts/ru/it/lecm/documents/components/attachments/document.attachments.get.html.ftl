<!-- Parameters and libs -->
<#assign el=args.htmlid/>

<!-- Markup -->
<div class="document-metadata-header document-details-panel">
    <h2 id="${el}-heading" class="thin dark">
        ${msg("heading")}
        <span class="alfresco-twister-actions">
            <a id="${el}-action-expand" href="javascript:void(0);" onclick="" class="expand" title="${msg("label.edit")}">&nbsp</a>
        </span>
    </h2>

    <div id="${el}-formContainer">
        Вложения будут здесь
    </div>
    <script type="text/javascript">//<![CDATA[
        (function () {
            function init() {
                Alfresco.util.createTwister("${el}-heading", "DocumentAttachments");

                new LogicECM.DocumentAttachments("${el}").setOptions(
                        {
                            nodeRef: "${nodeRef}",
                            title: "${msg('heading')}"
                        }).setMessages(${messages});
            }

            YAHOO.util.Event.onDOMReady(init);
        })();
    //]]></script>
</div>