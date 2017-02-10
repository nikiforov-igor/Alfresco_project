<label><b>${msg("lecm.meditor.lbl.aspects")}<b/></label>
<#include "/ru/it/lecm/controls/datatable.ftl">
<@inlineScript group='model-editor'>
(function () {
	function initAspectsDatatable(obj) {
		var columnDefinitions = [{
				key: 'expand',
				label: '',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatActions,
				width: 15,
				maxAutoWidth: 15
			}, {
				className: 'viewmode-label',
				key: 'aspect',
				label: '${msg("lecm.meditor.lbl.aspect")}',
				dropdownOptions: obj.aspects,
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatDropdown,
				width : 1042,
				maxAutoWidth : 1042
			}, {
				key: 'delete',
				label: '',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatActions,
				width: 15,
				maxAutoWidth: 15
			}],
			dialogElements = [{
				name: 'aspect',
				label: '${msg("lecm.meditor.lbl.aspect")}',
				type: 'select',
				options: obj.aspects,
				showdefault: false
			}],
			responseSchema = {
				fields: [{
					key: 'aspect'
				}]
			},
			data = obj.model.aspectsArray;

		new LogicECM.module.ModelEditor.DatatableControl('LogicECM.module.ModelEditor.AspectsDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			dialogElements: dialogElements,
			responseSchema: responseSchema,
			mode: '${form.mode}',
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initAspectsDatatable);
})();
</@>

