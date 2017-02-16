<label><b>${msg("lecm.meditor.lbl.attrs")}</b></label>
<#include "/ru/it/lecm/controls/datatable.ftl">
<@inlineScript group='model-editor'>
(function () {
	function initAttributesDatatable(obj) {
		var dTypes = ['',
				{ label: '${msg("lecm.meditor.lbl.any")}',      value: 'd:any'      },
				{ label: '${msg("lecm.meditor.lbl.text")}',     value: 'd:text'     },
				{ label: '${msg("lecm.meditor.lbl.content")}',  value: 'd:content'  },
				{ label: '${msg("lecm.meditor.lbl.integer")}',  value: 'd:int'      },
				{ label: '${msg("lecm.meditor.lbl.long")}',     value: 'd:long'     },
				{ label: '${msg("lecm.meditor.lbl.float")}',    value: 'd:float'    },
				{ label: '${msg("lecm.meditor.lbl.double")}',   value: 'd:double'   },
				{ label: '${msg("lecm.meditor.lbl.date")}',     value: 'd:date'     },
				{ label: '${msg("lecm.meditor.lbl.datetime")}', value: 'd:datetime' },
				{ label: '${msg("lecm.meditor.lbl.boolean")}',  value: 'd:boolean'  },
				{ label: '${msg("lecm.meditor.lbl.qname")}',    value: 'd:qname'    },
				{ label: '${msg("lecm.meditor.lbl.noderef")}',  value: 'd:noderef'  },
				{ label: '${msg("lecm.meditor.lbl.category")}', value: 'd:category' },
				{ label: '${msg("lecm.meditor.lbl.mltext")}',   value: 'd:mltext'}
			],
			dTokenised = ['',
				{ label: '${msg("lecm.meditor.lbl.yes")}',  value: 'true'  },
				{ label: '${msg("lecm.meditor.lbl.no")}',   value: 'false' },
				{ label: '${msg("lecm.meditor.lbl.both")}', value: 'both'  }
			],
			columnDefinitions = [{
				key: 'copy',
				label: '',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatActions,
				width: 15,
				maxAutoWidth: 15
			}, {
				className: 'viewmode-label',
				key: '_name',
				label: '${msg("lecm.meditor.lbl.name")}',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatText,
				width: 170,
				maxAutoWidth: 170
			}, {
				className: 'viewmode-label',
				key: 'title',
				label: '${msg("lecm.meditor.lbl.title")}',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatText,
				width: 170,
				maxAutoWidth: 170
			}, {
				className: 'viewmode-label',
				key: 'default',
				label: '${msg("lecm.meditor.lbl.default")}',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatText,
				width: 170,
				maxAutoWidth: 170
			}, {
				className: 'viewmode-label',
				key: 'type',
				label: '${msg("lecm.meditor.lbl.type")}',
				dropdownOptions: dTypes,
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatDropdown,
				width: 100,
				maxAutoWidth: 100
			}, {
				className: 'viewmode-label',
				key: 'mandatory',
				label: '${msg("lecm.meditor.lbl.mandatory")}',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatBoolean,
				width: 100,
				maxAutoWidth: 100
			}, {
				className: 'viewmode-label',
				key: '_enabled',
				label: '${msg("lecm.meditor.lbl.index")}',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatBoolean,
				width: 100,
				maxAutoWidth: 100
			}, {
				className: 'viewmode-label',
				key: 'tokenised',
				label: '${msg("lecm.meditor.lbl.tokenised")}',
				dropdownOptions: dTokenised,
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatDropdown,
				width: 100,
				maxAutoWidth: 100
			}, {
				key: 'delete',
				label: '',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatActions,
				width: 15,
				maxAutoWidth: 15
			}],
			dialogElements = {
				'_name': {
					name: '_name',
					label: '${msg("lecm.meditor.lbl.name")}',
					type: 'input',
					value: ''
				},
				'title': {
					name: 'title',
					label: '${msg("lecm.meditor.lbl.title")}',
					type: 'input',
					value: ''
				},
				'default': {
					name: 'default',
					label: '${msg("lecm.meditor.lbl.default")}',
					type: 'input',
					value: ''
				},
				'type': {
					name: 'type',
					label: '${msg("lecm.meditor.lbl.type")}',
					type: 'select',
					options: dTypes,
					showdefault: false
				},
				'mandatory': {
					name: 'mandatory',
					label: '${msg("lecm.meditor.lbl.mandatory")}',
					type: 'select',
					options: [
						{ label: '${msg("lecm.meditor.lbl.yes")}', value: 'true'  },
						{ label: '${msg("lecm.meditor.lbl.no")}',  value: 'false' }
					],
					value: 'false',
					showdefault: false
				},
				'_enabled': {
					name: '_enabled',
					label: '${msg("lecm.meditor.lbl.index")}',
					type: 'select',
					options: [
						{ label: '${msg("lecm.meditor.lbl.yes")}', value: 'true'  },
						{ label: '${msg("lecm.meditor.lbl.no")}',  value: 'false' }
					],
					value: 'false',
					showdefault: false
				},
				'tokenised': {
					name: 'tokenised',
					label: '${msg("lecm.meditor.lbl.tokenised")}',
					type: 'select',
					options: [
						{ label: '${msg("lecm.meditor.lbl.both")}', value: 'both' },
						{ label: '${msg("lecm.meditor.lbl.yes")}', value: 'true'  },
						{ label: '${msg("lecm.meditor.lbl.no")}', value: 'false'  }
					],
					value: 'both',
					showdefault: false
				}
			},
			responseSchema = {
				fields: [
					{ key: '_id'       },
					{ key: '_name'     },
					{ key: 'title'     },
					{ key: 'default'   },
					{ key: 'type'      },
					{ key: 'mandatory' },
					{ key: '_enabled'  },
					{ key: 'tokenised' },
					{ key: 'validator' }
				]
			},
			ns = obj.model.prop_namespace_name,
			data = obj.model.attributesArray;

		new LogicECM.module.ModelEditor.DatatableControl('LogicECM.module.ModelEditor.AttributesDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			dialogElements: dialogElements,
			responseSchema: responseSchema,
			mode: '${form.mode}',
			ns: ns,
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initAttributesDatatable);
})();
</@>
