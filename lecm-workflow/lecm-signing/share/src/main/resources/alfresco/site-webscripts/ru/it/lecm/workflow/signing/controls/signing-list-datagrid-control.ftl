<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign params = field.control.params/>

<#assign itemId = args.itemId/>

<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">
(function() {
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};

	function init() {
		var loader = new YAHOO.util.YUILoader({
			require: [
				"lecmDatagrid",
				"lecmSigningListDataGridControl"
			],
			skin: {}
		});

		loader.addModule({
			name: 'lecmDatagrid',
			type: 'js',
			fullpath: Alfresco.constants.URL_RESCONTEXT + 'scripts/lecm-base/components/lecm-datagrid.js'
		});

		loader.addModule({
			name: 'lecmSigningListDataGridControl',
			type: 'js',
			fullpath: Alfresco.constants.URL_RESCONTEXT + 'scripts/lecm-signing/signing-list-datagrid-control.js'
		});

		loader.onSuccess = createDatagrid;
		loader.insert();
	}

	function createDatagrid() {
		LogicECM.CurrentModules["signingListDatagridControl"] = new LogicECM.module.Signing.SigningListDatagridControl("${controlId}", "${itemId}");
		LogicECM.CurrentModules["signingListDatagridControl"].setMessages(${messages});
		LogicECM.CurrentModules["signingListDatagridControl"].setOptions({
			usePagination: false,
            datagridFormId: "${field.control.params.datagridFormId!"datagrid"}",
			showExtendSearchBlock: false,
			showCheckboxColumn: false,
			bubblingLabel: "SigningListDatagridControl",
			expandable: false,
			showActionColumn: false
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
</script>

<div class="viewmode-label" style="padding-top: 20px"><h3>${field.label?html}</h3></div>

<div id="${controlId}">
	<@grid.datagrid controlId false />
</div>
