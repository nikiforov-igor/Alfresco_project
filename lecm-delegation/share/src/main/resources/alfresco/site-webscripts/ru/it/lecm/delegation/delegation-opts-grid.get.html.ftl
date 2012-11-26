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

	YAHOO.util.Event.onContentReady ('${id}', function () {
		YAHOO.Bubbling.fire ("activeGridChanged", {
			datagridMeta:{
				itemType: LogicECM.module.Delegation.DELEGATION_OPTIONS_CONTAINER.itemType,
				nodeRef: LogicECM.module.Delegation.DELEGATION_OPTIONS_CONTAINER.nodeRef
			}
		});
	});
//]]>
</script>
</@grid.datagrid>
