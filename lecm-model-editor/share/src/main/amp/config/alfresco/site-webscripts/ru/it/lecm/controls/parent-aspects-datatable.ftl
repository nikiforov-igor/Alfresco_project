<label><b>Аспекты родительского документа</b></label>
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
	function initParentAspectsDatatable(obj) {
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
				dropdownOptions: obj.aspects,
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatText,
				width : 1078,
				maxAutoWidth : 1078
			}],
			dialogElements = [{
				name: 'aspect',
				label: '${msg("lecm.meditor.lbl.table")}',
				type: 'select',
				options: obj.aspects,
				showdefault: false
			}],
			responseSchema = {
				fields: [
				{key: 'name'},
				{key: 'aspectName'},
				{key: 'aspect'}
				]
			},
			nodeRef = '${context.properties.nodeRef}',
			doctype = '${context.properties.doctype}',
			data = obj.model.aspectsArray;

		new LogicECM.module.ModelEditor.RODatatableControl('LogicECM.module.ModelEditor.ParentAspectsDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			dialogElements: dialogElements,
			responseSchema: responseSchema,
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/type/parent/aspects?nodeRef='+nodeRef+'&doctype='+doctype,
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initParentAspectsDatatable);
})();
</@>

