<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
<#assign nodeRef = (context.page.properties["nodeRef"]!page.url.args.nodeRef)?js_string>
<#assign nodeType = context.page.properties["nodeType"]!"document">
<#assign fileName = (page.url.args.doctype!"")?html>
<div class="form-manager">
	<h1>${msg("lecm.meditor.lbl.document")}: ${fileName}</h1>
</div>
<@script type='text/javascript' src='${url.context}/res/components/model-editor/model-editor-form-manager.js' group='model-editor'/>
<@inlineScript group='model-editor'>
(function () {
	LogicECM.module.ModelEditor.ModelPromise = new LogicECM.module.Base.SimplePromise();
	new LogicECM.module.ModelEditor.FormManager('${args.htmlid}', {
		failureMessage: 'edit-metadata-mgr.update.failed',
		defaultUrl: '${siteURL(nodeType + "-details?nodeRef=" + nodeRef)}',
		args: {
			formId: '${context.page.properties.formId!page.url.args.formId}',
			nodeRef: '${context.page.properties.nodeRef!page.url.args.nodeRef}',
			redirect: '${context.page.properties.redirect!page.url.args.redirect}'
		}
	}, ${messages});
})();
</@>
