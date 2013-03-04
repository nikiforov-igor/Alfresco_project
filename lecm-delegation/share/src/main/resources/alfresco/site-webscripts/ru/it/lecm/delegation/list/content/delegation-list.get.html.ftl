<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = true/>

<script type="text/javascript">//<![CDATA[

	var datagrid = new LogicECM.module.Delegation.DelegationList.Grid('${id}');
	datagrid.setOptions({
		bubblingLabel: "delegation-list-datagrid",
		usePagination:true,
		showExtendSearchBlock:true,
		showCheckboxColumn: false,
		searchShowInactive: false,
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
				nodeRef: LogicECM.module.Delegation.Const.nodeRef,
			}
		});
	});

	function onShowOnlyConfiguredChanged () {
		var cbShowOnlyConfigured = YAHOO.util.Dom.get("cbShowOnlyConfigured");
		var obj = {
			datagridMeta: YAHOO.lang.merge (datagrid.datagridMeta, {
				searchShowInactive: !cbShowOnlyConfigured.checked
			})
		};
		YAHOO.Bubbling.fire ("activeGridChanged", obj);
	};
//]]>
</script>

<div align="right" style="padding-top: 0.5em;">
	<input type="checkbox" id="cbShowOnlyConfigured" style="vertical-align: middle; margin: auto 0;" onChange="onShowOnlyConfiguredChanged()" checked>
	<label class="checkbox" for="cbShowOnlyConfigured" style="vertical-align: middle; margin: auto 0;">Отображать только настроенные</label>
</div>
<@grid.datagrid id showViewForm/>
