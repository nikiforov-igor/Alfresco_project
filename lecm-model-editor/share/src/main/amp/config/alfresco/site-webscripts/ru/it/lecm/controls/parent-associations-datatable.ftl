<label><b>Ассоциации родительского документа</b></label>
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
	function initParentAssociationsDatatable(obj) {
		var columnDefinitions = [{
				key: 'expand',
				label: '',
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatActions,
				width: 15,
				maxAutoWidth: 15
			}, {
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
				key: 'class',
				label: '${msg("lecm.meditor.lbl.type")}',
				dropdownOptions: obj.associations,
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatDropdown,
				width: 291,
				maxAutoWidth: 291
			}, {
				className: 'viewmode-label',
				key: 'mandatory',
				label: '${msg("lecm.meditor.lbl.mandatory")}',
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatBoolean,
				width: 100,
				maxAutoWidth: 100
			}, {
				className: 'viewmode-label',
				key: 'many',
				label: '${msg("lecm.meditor.lbl.multiple")}',
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatBoolean,
				width: 260,
				maxAutoWidth: 260
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
					key: 'assocs'
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
			nodeRef = '${context.properties.nodeRef}',
			doctype = '${context.properties.doctype}',
			data = obj.model.associationsArray;

		new LogicECM.module.ModelEditor.RODatatableControl('LogicECM.module.ModelEditor.ParentAssociationsDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			dialogElements: dialogElements,
			responseSchema: responseSchema,
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/type/associations?nodeRef='+nodeRef+'&doctype='+doctype,
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initParentAssociationsDatatable);
})();
</@>
