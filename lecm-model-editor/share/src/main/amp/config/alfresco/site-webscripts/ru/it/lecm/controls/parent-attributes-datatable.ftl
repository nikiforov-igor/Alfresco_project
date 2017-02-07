<script language="javascript"> 
function toggle2() {
    var ele = document.getElementById("${fieldHtmlId}");
    var text = document.getElementById("${fieldHtmlId}-displayText");
    if(ele.style.display == "block") {
        ele.style.display = "none";
        text.style.background = "transparent url(/share/res/components/images/collapsed.png) no-repeat scroll center center";
    }
    else {
        ele.style.display = "block";
        text.style.background = "transparent url(/share/res/components/images/expanded.png) no-repeat scroll center center";
    }
} 
</script>
<label><b>Атрибуты родительского документа</b><a id="${fieldHtmlId}-displayText" href="javascript:toggle2();" class="down">&nbsp;</a></label>
<div class="control datatable models">
	<div class="container">
		<div id="${fieldHtmlId}" class="value-div" style="display: none;">
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
	function initParentAttributesDatatable(obj) {
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
				{ label: '${msg("lecm.meditor.lbl.yes")}',  value: 'TRUE'  },
				{ label: '${msg("lecm.meditor.lbl.no")}',   value: 'FALSE' },
				{ label: '${msg("lecm.meditor.lbl.both")}', value: 'BOTH'  }
			],
			columnDefinitions = [{
				className: 'viewmode-label',
				key: '_name',
				label: '${msg("lecm.meditor.lbl.name")}',
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatText,
				width: 170,
				maxAutoWidth: 170
			}, {
				className: 'viewmode-label',
				key: 'title',
				label: '${msg("lecm.meditor.lbl.title")}',
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatText,
				width: 170,
				maxAutoWidth: 170
			}, {
				className: 'viewmode-label',
				key: 'default',
				label: '${msg("lecm.meditor.lbl.default")}',
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatText,
				width: 170,
				maxAutoWidth: 170
			}, {
				className: 'viewmode-label',
				key: 'type',
				label: '${msg("lecm.meditor.lbl.type")}',
				dropdownOptions: dTypes,
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatDropdown,
				width: 100,
				maxAutoWidth: 100
			}, {
				className: 'viewmode-label',
				key: 'mandatory',
				label: '${msg("lecm.meditor.lbl.mandatory")}',
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatBoolean,
				width: 100,
				maxAutoWidth: 100
			}, {
				className: 'viewmode-label',
				key: '_enabled',
				label: '${msg("lecm.meditor.lbl.index")}',
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatBoolean,
				width: 100,
				maxAutoWidth: 100
			}, {
				className: 'viewmode-label',
				key: 'tokenised',
				label: '${msg("lecm.meditor.lbl.tokenised")}',
				dropdownOptions: dTokenised,
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatDropdown,
				width: 100,
				maxAutoWidth: 100
			}, {
				key: 'delete',
				label: '',
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatActions,
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
			nodeRef = '${context.properties.nodeRef}',
			doctype = '${context.properties.doctype}',
			data = obj.model.attributesArray;

		new LogicECM.module.ModelEditor.RODatatableControl('LogicECM.module.ModelEditor.ParentAttributesDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			dialogElements: dialogElements,
			responseSchema: responseSchema,
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/type/attributes?nodeRef='+nodeRef+'&doctype='+doctype,
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initParentAttributesDatatable);
})();
</@>
