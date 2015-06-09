<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign datagridId = id + "-dtgrd">
<script>
(function(){
	var deputyDatagrid = new LogicECM.module.Base.DataGrid('${datagridId}');
	deputyDatagrid.setMessages(${messages});
	deputyDatagrid.setOptions({
		overrideSortingWith: false,
		usePagination: false,
		dataSource: 'lecm/deputy/subjects',
		showExtendSearchBlock: false,
		showCheckboxColumn: false,
		bubblingLabel: '${datagridId}',
		expandable: false,
		showActionColumn: false
	});

	YAHOO.Bubbling.on('dataItemsDeleted', checkForDestroy.bind(deputyDatagrid));

	function checkForDestroy(layer, args) {
		var items = args[1].items;

		items.forEach(function(el) {
			if(this.datagridMeta.nodeRef == el.nodeRef) {
				this.destroy();
			}
		}, this);
	}

	YAHOO.util.Event.onContentReady('${datagridId}', function() {
		YAHOO.Bubbling.fire('activeGridChanged', {
			datagridMeta: {
				sort: 'lecm-orgstr:employee-last-name|true',
				useFilterByOrg: false,
				itemType: 'lecm-deputy:deputy',
				nodeRef: '${nodeRef}',
				datagridFormId: "subjects-nested-datagrid"
			},
			bubblingLabel: '${datagridId}'
		});
	}, this, true);

	LogicECM.CurrentModules = LogicECM.CurrentModules || {};
	LogicECM.CurrentModules['${id}'] = deputyDatagrid;
})();
</script>

<div id='${datagridId}' class="subjects-datagrid">
	<@grid.datagrid datagridId false />
</div>
