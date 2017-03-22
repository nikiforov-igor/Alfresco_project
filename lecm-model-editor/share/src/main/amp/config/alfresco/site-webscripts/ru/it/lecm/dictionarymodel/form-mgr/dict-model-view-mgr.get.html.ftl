<#assign fileName = (page.url.args.doctype!"")?html>
<div class="form-manager">
	<h1>${msg("lecm.meditor.lbl.dictionary")}: ${fileName}</h1>
</div>
<@script type='text/javascript' src='${url.context}/res/components/model-editor/model-editor-form-manager2.js' group='model-editor'/>
<@inlineScript group='model-editor'>
(function () {
	LogicECM.module.ModelEditor.ModelPromise = new LogicECM.module.Base.SimplePromise();
	new LogicECM.module.ModelEditor.FormManager2('${args.htmlid}', {
		args: {
			formId: '${context.page.properties.formId!page.url.args.formId}',
			redirect: '${context.page.properties.redirect!page.url.args.redirect}',
			itemId: '${context.page.properties.itemId!page.url.args.itemId}'
		}
	}, ${messages});
})();
</@>
