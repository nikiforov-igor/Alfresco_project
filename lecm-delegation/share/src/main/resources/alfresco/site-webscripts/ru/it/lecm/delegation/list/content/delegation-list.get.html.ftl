<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = true/>

<script type="text/javascript">//<![CDATA[
	(function () {
		"use strict";
		var datagrid = new LogicECM.module.Delegation.DelegationList.Grid('${id}');
		datagrid.setOptions({
			bubblingLabel: "delegation-list-datagrid",
			usePagination:true,
            disableDynamicPagination: true,
			showExtendSearchBlock:true,
			showCheckboxColumn: false,
			searchShowInactive: true,
			attributeForShow: "lecm-d8n:delegation-opts-owner-assoc",
			dataSource: "lecm/delegation/list",
			actions: [
				{
					type: "datagrid-action-link-delegation-list-datagrid",
					id: "onActionEdit",
					permission: "edit",
					label: "делегировать полномочия"
				}
			]
		});
		datagrid.setMessages(${messages});
		YAHOO.util.Event.onContentReady (datagrid.id, function () {
			YAHOO.Bubbling.fire ("activeGridChanged", {
				datagridMeta:{
					itemType: LogicECM.module.Delegation.Const.itemType,
					nodeRef: LogicECM.module.Delegation.Const.nodeRef
				}
			});
		});
	})();
//]]>
</script>

<@grid.datagrid id showViewForm/>
