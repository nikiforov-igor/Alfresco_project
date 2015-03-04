<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign datagridId = id + "-dtgrd">
<script>
(function(){
	var procuraciesDatagrid = new LogicECM.module.Base.DataGrid('${datagridId}');
	procuraciesDatagrid.setMessages(${messages});
	procuraciesDatagrid.setOptions({
		usePagination: false,
		showExtendSearchBlock: false,
		showCheckboxColumn: false,
		bubblingLabel: '${datagridId}',
		expandable: false,
		showActionColumn: false,
		attributeForShow: 'lecmApprovalResult:approvalResultItemDecision'
	});

	YAHOO.util.Event.onContentReady('${datagridId}', function() {
		YAHOO.Bubbling.fire('activeGridChanged', {
			datagridMeta: {
				 useFilterByOrg: false,
				itemType: '${itemType}',
				nodeRef: '${nodeRef}',
				useChildQuery: true,
				searchConfig: {
					filter: '${filter}'
				}
			},
			bubblingLabel: '${datagridId}'
		});
	}, this, true);

	LogicECM.CurrentModules = LogicECM.CurrentModules || {};
	LogicECM.CurrentModules['${id}'] = procuraciesDatagrid;
})();
</script>

<div id='${datagridId}' class="procuracy-datagrid">
	<@grid.datagrid datagridId false />
</div>
