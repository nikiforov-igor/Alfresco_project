<!-- Parameters and libs -->
<#assign el=args.htmlid/>

<!-- Markup -->
<div class="document-metadata-header document-details-panel">
    <h2 id="${el}-heading" class="thin dark">
        ${msg("heading")}
    </h2>
    <div id="${el}-formContainer">
        <div id="${el}-form" style="display:none"></div>
        <a id="${el}-link" href="javascript:void(0);" onclick="" class="edit" title="${msg("label.connections.more")}">${msg("label.connections.more")}</a>
    </div>

    <script type="text/javascript">//<![CDATA[
        Alfresco.util.createTwister("${el}-heading", "DocumentConnections");

        new LogicECM.DocumentConnections("${el}").setOptions(
                {
                    nodeRef: "${nodeRef}",
                    title:"${msg('heading')}"
            }).setMessages(${messages});
    //]]></script>
</div>