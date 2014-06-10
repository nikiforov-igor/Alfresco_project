<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.ShareFormManager("${args.htmlid}").setOptions(
   {
      failureMessage: "edit-metadata-mgr.update.failed",
      defaultUrl: "${siteURL((nodeType!"document") + "-details?nodeRef=" + (nodeRef!page.url.args.nodeRef)?js_string)}"
   }).setMessages(${messages});
//]]></script>
<div class="form-manager">
   <h1>Документ: ${fileName?html}</h1>
</div>