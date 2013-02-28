<!-- Parameters and libs -->
<#assign el=args.htmlid/>

<!-- Markup -->
<div class="document-metadata-header document-details-panel">
    <h2 id="${el}-heading" class="thin dark">
        ${msg("heading")}
    </h2>
    <div id="${el}-formContainer">Связи будут здесь</div>
    <script type="text/javascript">//<![CDATA[
    Alfresco.util.createTwister("${el}-heading", "DocumentLinks");
    //]]></script>
</div>