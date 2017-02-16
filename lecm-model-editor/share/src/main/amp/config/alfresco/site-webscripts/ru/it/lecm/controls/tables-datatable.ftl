<label><b>${msg("lecm.meditor.lbl.tables")}<b/></label>
<#include "/ru/it/lecm/controls/datatable.ftl">
<@inlineScript group='model-editor'>
(function () {
	function initTablesDatatable(obj) {
		var columnDefinitions = [{
				key: 'expand',
				label: '',
				formatter: LogicECM.module.ModelEditor.DatatableControl.prototype.formatActions,
				width: 15,
				maxAutoWidth: 15
			}, {
				className: 'viewmode-label',
				key: 'table',
				label: '${msg("lecm.meditor.lbl.table")}',
				dropdownOptions: obj.tables,
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
			data = obj.model.tablesArray;

		new LogicECM.module.ModelEditor.DatatableControl('LogicECM.module.ModelEditor.TablesDatatable', '${fieldHtmlId}', {
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

	LogicECM.module.ModelEditor.ModelPromise.then(initTablesDatatable);
})();
</@>

