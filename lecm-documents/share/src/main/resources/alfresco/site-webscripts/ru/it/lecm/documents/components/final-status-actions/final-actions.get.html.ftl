<#if documentDetailsJSON??>
   <#assign el=args.htmlid?js_string>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.DocumentActions("${el}").setOptions(
      {
         nodeRef: "${nodeRef?js_string}",
         siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
         containerId: "${container?js_string}",
         rootNode: "${rootNode}",
         replicationUrlMapping: ${replicationUrlMappingJSON!"{}"},
         documentDetails: ${documentDetailsJSON},
         repositoryBrowsing: ${(rootNode??)?string}
      }).setMessages(
         ${messages}
      );
   //]]></script>

   <div id="${el}">
   <div id="${el}-body" class="widget-panel-grey document-final-actions document-details-panel">
      <h2 id="${el}-heading" class="dark">
         ${msg("heading")}
      </h2>
      <div class="doclist">
         <div id="${el}-actionSet" class="action-set"></div>
      </div>
   </div>

   <script type="text/javascript">//<![CDATA[
      Alfresco.util.createTwister("${el}-heading", "DocumentActions");
   //]]></script>
</#if>
