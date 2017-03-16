<div class="control content hidden">
	<div class="container">
		<div class="value-div">
			<input id="${fieldHtmlId}" name="${field.name}" type="hidden" value="${field.value}">
		</div>
	</div>
</div>
<div class="clear"></div>
<@script type='text/javascript' src='${url.context}/res/components/model-editor/controls/content.js' group='model-editor'/>
<@inlineScript group='model-editor'>
(function () {
	new LogicECM.module.ModelEditor.ContentControl('${fieldHtmlId}', {
		itemKind: '${form.arguments.itemKind}',
		itemId: '${form.arguments.itemId}',
		doctype: '${context.properties.doctype}'
	}, ${messages});
})();
</@>
