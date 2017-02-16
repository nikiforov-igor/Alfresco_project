<label><b>Таблицы родительского документа</b></label>
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
	function initParentTablesDatatable(obj) {
		var columnDefinitions = [{
				key: 'expand',
				label: '',
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatActions,
				width: 15,
				maxAutoWidth: 15
			}, {
				className: 'viewmode-label',
				key: 'aspectName',
				label: '${msg("lecm.meditor.lbl.table")}',
				dropdownOptions: obj.tables,
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatText,
				width : 1078,
				maxAutoWidth : 1078
			}],
			responseSchema = {
				fields: [
				{key: 'name'},
				{key: 'aspectName'},
				{key: 'aspectTitle'},
				{key: 'table'}
				]
			},
			nodeRef = '${context.properties.nodeRef}',
			doctype = '${context.properties.doctype}',
			associations = obj.associations,
			data = obj.model.tablesArray;

		new LogicECM.module.ModelEditor.RODatatableControl('LogicECM.module.ModelEditor.ParentTablesDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			responseSchema: responseSchema,
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/type/parent/tables?nodeRef='+nodeRef+'&doctype='+doctype,
			associations: associations,
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initParentTablesDatatable);
})();
</@>

