<#if attachmentDetailsJSON??>
   <#assign el=args.htmlid?js_string>
   <script type="text/javascript">//<![CDATA[
      new LogicECM.DocumentAttachmentActions("${el}").setOptions(
      {
         nodeRef: "${nodeRef?js_string}",
         siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
         containerId: "${container?js_string}",
         rootNode: "${rootNode}",
         replicationUrlMapping: ${replicationUrlMappingJSON!"{}"},
         documentDetails: ${attachmentDetailsJSON},
         repositoryBrowsing: ${(rootNode??)?string},
	     documentNodeRef: "${document.nodeRef!''}"
      }).setMessages(
         ${messages}
      );
   //]]></script>

   <div class="widget-bordered-panel">
       <div id="${el}-body" class="document-actions document-components-panel">
           <h2 id="${el}-heading" class="dark">
               ${msg("heading")}
           </h2>
           <div class="doclist">
               <div id="${el}-actionSet" class="action-set"></div>
           </div>
       </div>
   </div>

   <script type="text/javascript">//<![CDATA[
      Alfresco.util.createTwister("${el}-heading", "DocumentActions");
   //]]></script>
</#if>
