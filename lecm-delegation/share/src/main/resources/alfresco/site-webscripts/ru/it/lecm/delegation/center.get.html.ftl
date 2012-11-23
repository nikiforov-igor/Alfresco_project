<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = false/>

<@grid.datagrid id showViewForm>
<script type="text/javascript">//<![CDATA[
	var datagrid = new LogicECM.module.Base.DataGrid('${id}');
	datagrid.setOptions({
		usePagination:true,
		showExtendSearchBlock:true
	});
	datagrid.setMessages(${messages});
	datagrid.onActionRevoke = function () {
		Alfresco.util.PopupManager.displayMessage ({
			text: "onActionRevoke"
		});
	};
	datagrid.onActionPropogate = function () {
		Alfresco.util.PopupManager.displayMessage ({
			text: "onActionPropogate"
		});
	};
	YAHOO.util.Event.onContentReady ('${id}', function () {
		YAHOO.Bubbling.fire ("activeGridChanged", {
			datagridMeta:{
				itemType: "lecm-ba:procuracy",
				nodeRef: LogicECM.module.Delegation.DELEGATION_ROOT
				//filter:"PARENT:\"" + LogicECM.module.Delegation.DELEGATION_ROOT + "\""
			}
		});
	});
//]]>
</script>
</@grid.datagrid>
