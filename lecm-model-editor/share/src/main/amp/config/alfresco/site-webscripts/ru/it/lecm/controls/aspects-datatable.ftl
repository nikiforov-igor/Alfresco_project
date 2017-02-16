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
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatTree,
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
			dTokenised = ['',
				{ label: '${msg("lecm.meditor.lbl.yes")}',  value: 'TRUE'  },
				{ label: '${msg("lecm.meditor.lbl.no")}',   value: 'FALSE' },
				{ label: '${msg("lecm.meditor.lbl.both")}', value: 'BOTH'  }
			],
			dTypes = ['',
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
				{ label: '${msg("lecm.meditor.lbl.mltext")}',   value: 'd:mltext'   },
				{ label: '${msg("lecm.meditor.lbl.locale")}',   value: 'd:locale'   }
			],
			associations = obj.associations,
			data = obj.model.aspectsArray;

		new LogicECM.module.ModelEditor.DatatableControl('LogicECM.module.ModelEditor.AspectsDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			dialogElements: dialogElements,
			responseSchema: responseSchema,
			mode: '${form.mode}',
			dTokenised: dTokenised,
			dTypes: dTypes,
			associations: associations,
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initAspectsDatatable);
})();
</@>

