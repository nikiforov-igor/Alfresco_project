<#include "/org/alfresco/components/form/form.dependencies.inc">
<!-- Document Metadata Header -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/document-details/document-metadata.css" />
<@script type="text/javascript" src="${url.context}/res/components/document-details/document-metadata.js"></@script>


<#if document??>
    <!-- Parameters and libs -->
    <#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
    <#assign el=args.htmlid/>

    <!-- Markup -->
<div class="widget-bordered-panel">
    <div class="document-metadata-header document-components-panel">
        <h2 id="${el}-heading" class="dark">
            ${msg("heading")}
            <#if allowMetaDataUpdate!false>
                <span class="alfresco-twister-actions">
                    <a href="${siteURL("edit-metadata?nodeRef=" + nodeRef?url)}" class="edit" title="${msg("label.edit")}"> &nbsp;</a>
                </span>
            </#if>
        </h2>
        <div id="${el}-formContainer"></div>
    </div>
</div>

    <script type="text/javascript">//<![CDATA[
    Alfresco.util.createTwister("${el}-heading", "DocumentMetadata");
    //]]></script>

   <!-- Javascript instance -->
   <script type="text/javascript">//<![CDATA[
      new Alfresco.DocumentMetadata("${el}").setOptions(
      {
         nodeRef: "${nodeRef}",
         site: <#if site??>"${site?js_string}"<#else>null</#if>,
         formId: <#if formId??>"${formId?js_string}"<#else>null</#if>
      }).setMessages(
         ${messages}
      );
   //]]></script>
</#if>