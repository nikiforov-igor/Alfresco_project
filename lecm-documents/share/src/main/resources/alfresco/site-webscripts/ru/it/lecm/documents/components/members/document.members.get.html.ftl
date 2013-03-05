<#assign el=args.htmlid/>

<div class="document-metadata-header document-details-panel">
    <h2 id="${el}-heading" class="thin dark">
        ${msg("heading")}
        <span class="alfresco-twister-actions">
            <a id="${el}-action-expand" href="javascript:void(0);" onclick="" class="expand" title="${msg("label.expand")}">&nbsp</a>
         </span>
    </h2>
    <div id="${el}-formContainer">Участники будут здесь</div>
    <script type="text/javascript">//<![CDATA[
    (function () {
        Alfresco.util.createTwister("${el}-heading", "DocumentMembers");
        var documentMembers;
        function init() {
            documentMembers = new LogicECM.DocumentMembers("${el}").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        title: "${msg('heading')}",
                        dashletId: "template_x002e_members_x002e_document_x0023_default"
                    }).setMessages(${messages});
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
    </script>
</div>