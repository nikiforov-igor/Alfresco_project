<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showSearchBlock = false/>
<#assign showViewForm = false/>
<#assign viewFormId = ""/>

<@grid.datagrid id showSearchBlock showViewForm viewFormId>
<script type="text/javascript">//<![CDATA[
	var datagrid = new LogicECM.module.Base.DataGrid('${id}');
	datagrid.setOptions({
		usePagination:true,
		showExtendSearchBlock:${showSearchBlock?string}
	});
	datagrid.setMessages(${messages});
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
