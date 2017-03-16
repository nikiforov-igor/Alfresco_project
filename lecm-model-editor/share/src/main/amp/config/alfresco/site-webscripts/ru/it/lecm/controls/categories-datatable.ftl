<label><b>${field.label?html}</b></label>
<#include "/ru/it/lecm/controls/datatable.ftl">
<@inlineScript group='model-editor'>
(function () {
	function initCategoriesDatatable(obj) {
		var columnDefinitions = [{
				key: 'name',
				className: 'viewmode-label',
				label: '${msg("lecm.meditor.lbl.category")}',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatText,
				width: 360,
				maxAutoWidth: 360
			}, {
				key : 'delete',
				label : '',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatActions,
				width: 15,
				maxAutoWidth: 15
			}],
			dialogElements = {
				name: {
					name: 'name',
					label: '${msg("lecm.meditor.lbl.category")}',
					type: 'input',
					value: ''
				}
			},
			responseSchema = {
				fields: [{
					key: "name"
				}]
			},
			data = obj.model.categoryArray;

		new LogicECM.module.ModelEditor.DatatableControl('LogicECM.module.ModelEditor.CategoriesDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			dialogElements: dialogElements,
			responseSchema: responseSchema,
			mode: '${form.mode}',
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initCategoriesDatatable);
})();
</@>

