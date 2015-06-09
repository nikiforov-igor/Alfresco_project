<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign datagridId = id + "-dtgrd">
<script>
(function(){
	var deputyDatagrid = new LogicECM.module.Base.DataGrid('${datagridId}');
	deputyDatagrid.setMessages(${messages});
	deputyDatagrid.setOptions({
		overrideSortingWith: false,
		usePagination: false,
		dataSource: 'lecm/deputy/list',
		showExtendSearchBlock: false,
		showCheckboxColumn: false,
		bubblingLabel: '${datagridId}',
		expandable: false,
		showActionColumn: false
	});

	YAHOO.util.Event.onContentReady('${datagridId}', function() {
		YAHOO.Bubbling.fire('activeGridChanged', {
			datagridMeta: {
				sort: 'lecm-orgstr:employee-last-name|true',
				useFilterByOrg: false,
				itemType: '${itemType}',
				nodeRef: '${nodeRef}',
				datagridFormId: "deputy-nested-datagrid"
			},
			bubblingLabel: '${datagridId}'
		});
	}, this, true);

	LogicECM.CurrentModules = LogicECM.CurrentModules || {};
	LogicECM.CurrentModules['${id}'] = deputyDatagrid;
})();
</script>

<div id='${datagridId}' class="secretary-datagrid">
	<@grid.datagrid datagridId false />
</div>
