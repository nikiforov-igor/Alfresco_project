<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>

<@grid.datagrid id true>

<script type="text/javascript">//<![CDATA[

//var datagrid = new LogicECM.module.OrgStructure.BusinessRoles.DataGrid ("${id}");
var datagrid = new LogicECM.module.Base.DataGrid ("${id}");
datagrid.setOptions ({
	usePagination:true,
	showExtendSearchBlock:true,
	showCheckboxColumn: false,
	editForm: "configureBusinessRole",
	attributeForShow: "cm:name",
	actions: [{
		type:"datagrid-action-link-businessRole",
		id:"onActionEdit",
		permission:"edit",
		label:"${msg("actions.edit")}"
		}
	]
});
datagrid.setMessages (${messages});
//]]>
</script>

</@grid.datagrid>
