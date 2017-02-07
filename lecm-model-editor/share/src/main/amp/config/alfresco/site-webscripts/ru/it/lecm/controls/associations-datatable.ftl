<label><b>${msg("lecm.meditor.lbl.assocs")}<b/></label>
<#include "/ru/it/lecm/controls/datatable.ftl">
<@inlineScript group='model-editor'>
(function () {
	function initAssociationsDatatable(obj) {
		var columnDefinitions = [{
				className: 'viewmode-label',
				key: '_name',
				label: '${msg("lecm.meditor.lbl.name")}',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatText,
				width: 170,
				maxAutoWidth: 170
			}, {
				key: 'copy',
				label: '',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatActions,
				width: 15,
				maxAutoWidth: 15
			}, {
				className: 'viewmode-label',
				key: 'title',
				label: '${msg("lecm.meditor.lbl.title")}',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatText,
				width: 170,
				maxAutoWidth: 170
			}, {
				className: 'viewmode-label',
				key: 'class',
				label: '${msg("lecm.meditor.lbl.type")}',
				dropdownOptions: obj.associations,
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatDropdown,
				width: 291,
				maxAutoWidth: 291
			}, {
				className: 'viewmode-label',
				key: 'mandatory',
				label: '${msg("lecm.meditor.lbl.mandatory")}',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatBoolean,
				width: 100,
				maxAutoWidth: 100
			}, {
				className: 'viewmode-label',
				key: 'many',
				label: '${msg("lecm.meditor.lbl.multiple")}',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatBoolean,
				width: 223,
				maxAutoWidth: 223
			}, {
				key: 'delete',
				label: '',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatActions,
				width: 15,
				maxAutoWidth: 15
			}],
			dialogElements = [
				{
					name: '_name',
					label: '${msg("lecm.meditor.lbl.name")}',
					type: 'input'
				}, {
					name: 'title',
					label: '${msg("lecm.meditor.lbl.title")}',
					type: 'input'
				}, {
					name: 'class',
					label: '${msg("lecm.meditor.lbl.type")}',
					type:'select',
					options: obj.associations,
					showdefault: false
				}, {
					name: 'mandatory',
					label: '${msg("lecm.meditor.lbl.mandatory")}',
					type: 'select',
					options: [
						{ label: '${msg("lecm.meditor.lbl.yes")}', value:'true'  },
						{ label: '${msg("lecm.meditor.lbl.no")}',  value:'false' }
					],
					value: 'false',
					showdefault: false
				}, {
					name: 'many',
					label: '${msg("lecm.meditor.lbl.multiple")}',
					type:'select',
					options: [
						{ label: '${msg("lecm.meditor.lbl.yes")}', value: 'true'  },
						{ label: '${msg("lecm.meditor.lbl.no")}',  value: 'false' }
					],
					value: 'false',
					showdefault: false
				}
			],
			responseSchema = {
				fields: [{
					key: '_name'
				}, {
					key: 'class'
				}, {
					key: 'title'
				}, {
					key : 'mandatory'
				}, {
					key : 'many'
				}]
			},
			ns = obj.model.prop_namespace_name
			data = obj.model.associationsArray;

		new LogicECM.module.ModelEditor.DatatableControl('LogicECM.module.ModelEditor.AssociationsDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			dialogElements: dialogElements,
			responseSchema: responseSchema,
			mode: '${form.mode}',
			ns: ns,
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initAssociationsDatatable);
})();
</@>
