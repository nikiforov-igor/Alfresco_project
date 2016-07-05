<#include "/ru/it/lecm/controls/datatable.ftl">
<@inlineScript group='model-editor'>
(function () {
	function initTablesDatatable(obj) {
		var columnDefinitions = [{
				className: 'viewmode-label',
				key: 'table',
				label: '${msg("lecm.meditor.lbl.table")}',
				dropdownOptions: obj.tables,
				formatter: 'dropdown',
				width : 737,
				maxAutoWidth : 737
			}, {
				key: 'delete',
				label: '',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatActions,
				width: 15,
				maxAutoWidth: 15
			}],
			dialogElements = [{
				name: 'table',
				label: '${msg("lecm.meditor.lbl.table")}',
				type: 'select',
				options: obj.tables,
				showdefault: false
			}],
			responseSchema = {
				fields: [{
					key: 'table'
				}]
			},
			data = obj.model.tablesArray;

		new LogicECM.module.ModelEditor.DatatableControl('LogicECM.module.ModelEditor.TablesDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			dialogElements: dialogElements,
			responseSchema: responseSchema,
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initTablesDatatable);
})();
</@>
