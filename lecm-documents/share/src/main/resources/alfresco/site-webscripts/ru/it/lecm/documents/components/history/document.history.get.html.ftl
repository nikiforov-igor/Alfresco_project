<!-- Parameters and libs -->
<#assign el=args.htmlid/>

<!-- Markup -->
<div class="document-metadata-header document-details-panel">
    <h2 id="${el}-heading" class="thin dark">
    ${msg("heading")}
    </h2>

    <div id="${el}-formContainer">
        <a id="${el}-link" href="javascript:void(0);" onclick="" class="edit" title="Показать историю">Показать историю</a>
    </div>
    <script type="text/javascript">//<![CDATA[
    Alfresco.util.createTwister("${el}-heading", "DocumentHistory");

    new LogicECM.DocumentHistory("${el}").setOptions(
            {
                nodeRef: "${nodeRef}"
            }).setMessages(${messages});
    //]]></script>
</div>