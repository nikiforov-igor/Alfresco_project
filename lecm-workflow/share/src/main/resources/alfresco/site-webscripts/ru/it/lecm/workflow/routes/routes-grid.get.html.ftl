<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = true/>

<script type="text/javascript">
var datagrid = new LogicECM.module.Routes.DataGrid("${id}");
datagrid.setOptions({
	usePagination:true,
	showExtendSearchBlock: false,
	showCheckboxColumn: false,
	bubblingLabel: LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL,
	dataSource: "/lecm/workflow/routes/routesList",
	actions: [{
		type:"datagrid-action-link-" + LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL,
		id:"onActionEdit",
		permission:"edit",
		label:"${msg('actions.edit')}"
	},{
		type:"datagrid-action-link-" + LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL,
		id:"onActionDelete",
		permission:"delete",
		label:"${msg('actions.delete-row')}"
	}]
});
datagrid.setMessages(${messages});
YAHOO.util.Event.onContentReady("${id}", function () {
	YAHOO.Bubbling.fire ("activeGridChanged", {
		datagridMeta:{
			itemType: LogicECM.module.Routes.Const.ROUTES_CONTAINER.itemType,
			nodeRef: LogicECM.module.Routes.Const.ROUTES_CONTAINER.nodeRef,
			searchConfig: {
				filter: ""
			}
		},
		bubblingLabel: LogicECM.module.Routes.Const.ROUTES_DATAGRID_LABEL
	});
});
</script>

<@grid.datagrid id showViewForm/>
