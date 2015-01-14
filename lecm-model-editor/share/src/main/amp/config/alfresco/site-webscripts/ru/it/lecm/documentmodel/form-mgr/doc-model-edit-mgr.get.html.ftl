<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
<#assign nodeRef = (context.page.properties["nodeRef"]!page.url.args.nodeRef)?js_string>
<#assign nodeType = context.page.properties["nodeType"]!"document">
<#assign fileName = (context.page.properties["fileName"]!"")?html>
<script type="text/javascript">//<![CDATA[
	new Alfresco.component.ShareFormManager('${args.htmlid}').setOptions({
		failureMessage: 'edit-metadata-mgr.update.failed',
		defaultUrl: '${siteURL(nodeType + "-details?nodeRef=" + nodeRef)}'
	}).setMessages(${messages});
//]]></script>
<div class="form-manager">
	<h1>Документ: ${fileName}</h1>
</div>
