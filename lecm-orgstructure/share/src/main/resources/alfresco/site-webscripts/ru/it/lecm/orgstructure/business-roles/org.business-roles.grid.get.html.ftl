<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>

<@grid.datagrid id true>

<script type="text/javascript">//<![CDATA[

var datagrid = new LogicECM.module.OrgStructure.BusinessRoles.DataGrid ("${id}");
datagrid.setOptions ({
	usePagination:true,
	showExtendSearchBlock:true,
	showCheckboxColumn: false,
	actions: [{
		type:"action-link-businessRole",
		id:"onActionEdit",
		permission:"edit",
		label:"${msg("actions.edit")}"
		}
	]
});
datagrid.setMessages (${messages});

YAHOO.util.Event.onContentReady ("view-node-form", createDialog);
//]]>
</script>

</@grid.datagrid>
