<#assign el=args.htmlid/>

<div class="document-metadata-header document-details-panel">
    <h2 id="${el}-heading" class="thin dark">
    ${msg("heading")}
        <span class="alfresco-twister-actions">
            <a id="${el}-action-expand" href="javascript:void(0);" onclick="" class="expand" title="${msg("label.expand")}">&nbsp</a>
         </span>
    </h2>

    <div id="${el}-formContainer">Участники будут здесь</div>
    <script type="text/javascript">
        var documentMembersComponent = null;
    </script>
    <script type="text/javascript">//<![CDATA[
    (function () {
        Alfresco.util.createTwister("${el}-heading", "DocumentMembers");

        function init() {
            documentMembersComponent = new LogicECM.DocumentMembers("${el}").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        title: "${msg('heading')}"
                    }).setMessages(${messages});
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
    </script>
</div>