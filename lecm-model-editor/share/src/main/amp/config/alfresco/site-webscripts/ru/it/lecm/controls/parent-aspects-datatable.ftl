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
			responseSchema = {
				fields: [
				{key: 'name'},
				{key: 'aspectName'},
				{key: 'aspectTitle'},
				{key: 'aspect'}
				]
			},
			nodeRef = '${context.properties.nodeRef}',
			doctype = '${context.properties.doctype}',
			associations = obj.associations,
			data = obj.model.aspectsArray;

		new LogicECM.module.ModelEditor.RODatatableControl('LogicECM.module.ModelEditor.ParentAspectsDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			responseSchema: responseSchema,
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/type/parent/aspects?nodeRef='+nodeRef+'&doctype='+doctype,
			dTypes: dTypes,
			dTokenised: dTokenised,
			associations: associations,
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initParentAspectsDatatable);
})();
</@>

