<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign params = field.control.params/>

<#assign itemId = args.itemId/>

<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">
(function() {
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};

	function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-signing/signing-list-datagrid-control.js'
		],
        [
	        'css/lecm-signing/signing-list-datagrid-control.css'
        ], createDatagrid);
	}

	function createDatagrid() {
		var controlId = '${controlId}';
		LogicECM.CurrentModules[controlId] = new LogicECM.module.Signing.SigningListDatagridControl(controlId, '${itemId}');
		LogicECM.CurrentModules[controlId].setMessages(${messages});
		LogicECM.CurrentModules[controlId].setOptions({
			usePagination: false,
			datagridFormId: '${field.control.params.datagridFormId!"datagrid"}',
			showExtendSearchBlock: false,
			showCheckboxColumn: false,
			bubblingLabel: 'SigningListDatagridControl',
			expandable: false,
			showActionColumn: false
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
</script>

<div class="viewmode-label signing-list-table-lable"><h3>${field.label?html}</h3></div>

<div id="${controlId}">
	<@grid.datagrid controlId false />
</div>
