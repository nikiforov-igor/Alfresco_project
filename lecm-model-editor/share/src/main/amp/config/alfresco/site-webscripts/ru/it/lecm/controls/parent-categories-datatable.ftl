<label><b>Категории вложений родительского документа</b></label>
<div class="control datatable models">
	<div class="container">
		<div id="${fieldHtmlId}" class="value-div">
			<div id="${fieldHtmlId}-dialog"></div>
			<div id="${fieldHtmlId}-button-add"></div>
			<div id="${fieldHtmlId}-datatable"></div>
		</div>
	</div>
</div>
<div class="clear"></div>
<@script type='text/javascript' src='${url.context}/res/components/model-editor/controls/input.js' group='model-editor'/>
<@script type='text/javascript' src='${url.context}/res/components/model-editor/controls/select.js' group='model-editor'/>
<@script type='text/javascript' src='${url.context}/res/components/model-editor/controls/dialog.js' group='model-editor'/>
<@script type='text/javascript' src='${url.context}/res/components/model-editor/controls/readonlyDatatable.js' group='model-editor'/>
<@inlineScript group='model-editor'>
(function () {
	function initParentCategoriesDatatable(obj) {
		var columnDefinitions = [{
				key: 'name',
				className: 'viewmode-label',
				label: '${msg("lecm.meditor.lbl.category")}',
				width: 400,
				maxAutoWidth: 400
			}],
			responseSchema = {
				fields: [{
					key: "name"
				}]
			},
			nodeRef = '${context.properties.nodeRef}',
			doctype = '${context.properties.doctype}',
			data = [{"name":"test"}];

		new LogicECM.module.ModelEditor.RODatatableControl('LogicECM.module.ModelEditor.ParentCategoriesDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			responseSchema: responseSchema,
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/type/categories?nodeRef='+nodeRef+'&doctype='+doctype,
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initParentCategoriesDatatable);
})();
</@>