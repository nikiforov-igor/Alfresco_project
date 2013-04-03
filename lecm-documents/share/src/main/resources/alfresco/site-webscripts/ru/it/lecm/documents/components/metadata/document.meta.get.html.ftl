<#if document??>
<!-- Parameters and libs -->
    <#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
    <#assign el=args.htmlid/>

<!-- Markup -->
<div class="widget-bordered-panel">
    <div class="document-metadata-header document-components-panel">
        <h2 id="${el}-heading" class="thin dark">
        ${msg("heading")}
            <span class="alfresco-twister-actions">
            <a id="${el}-link" href="javascript:void(0);" onclick="" class="expand" title="${msg("label.view")}">&nbsp;</a>
         </span>
        </h2>
        <div id="${el}-formContainer"></div>
    </div>
</div>

<!-- Javascript instance -->
<script type="text/javascript">//<![CDATA[
    new Alfresco.DocumentMetadata("${el}").setOptions(
            {
                nodeRef: "${nodeRef}",
                site: null,
                formId: <#if formId??>"${formId?js_string}"<#else>null</#if>
            }).setMessages(${messages});
    var documentMetadataComponent = new window.LogicECM.DocumentMetadata("${el}").setOptions(
            {
                nodeRef: "${nodeRef}",
                title:"${msg('heading')}"
            }).setMessages(${messages});
//]]></script>
</#if>