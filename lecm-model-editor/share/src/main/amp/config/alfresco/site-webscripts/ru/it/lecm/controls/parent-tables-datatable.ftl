<script language="javascript"> 
function toggle3() {
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
<label><b>Таблицы родительского документа</b><a id="${fieldHtmlId}-displayText" href="javascript:toggle3();" class="down">&nbsp;</a></label>
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
	function initParentTablesDatatable(obj) {
		var columnDefinitions = [{
				className: 'viewmode-label',
				key: 'name',
				label: '${msg("lecm.meditor.lbl.table")}',
				dropdownOptions: obj.tables,
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatDropdown,
				width : 737,
				maxAutoWidth : 737
			}, {
				key: 'delete',
				label: '',
				formatter: LogicECM.module.ModelEditor.RODatatableControl.prototype.formatActions,
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
					key: 'name'
				}]
			},
			nodeRef = '${context.properties.nodeRef}',
			doctype = '${context.properties.doctype}',
			data = obj.model.tablesArray;

		new LogicECM.module.ModelEditor.RODatatableControl('LogicECM.module.ModelEditor.ParentTablesDatatable', '${fieldHtmlId}', {
			columnDefinitions: columnDefinitions,
			dialogElements: dialogElements,
			responseSchema: responseSchema,
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/type/aspects?nodeRef='+nodeRef+'&doctype='+doctype,
			data: data
		}, ${messages});
	}

	LogicECM.module.ModelEditor.ModelPromise.then(initParentTablesDatatable);
})();
</@>

