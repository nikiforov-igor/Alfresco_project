<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = true/>

<script type="text/javascript">//<![CDATA[

	var datagrid = new LogicECM.module.Delegation.DelegationList.Grid('${id}');
	datagrid.setOptions({
		usePagination:true,
		showExtendSearchBlock:true
	});
	datagrid.setMessages(${messages});

	YAHOO.util.Event.onContentReady ('${id}', function () {
		YAHOO.Bubbling.fire ("activeGridChanged", {
			datagridMeta:{
				itemType: LogicECM.module.Delegation.DELEGATION_OPTIONS_CONTAINER.itemType,
				nodeRef: LogicECM.module.Delegation.DELEGATION_OPTIONS_CONTAINER.nodeRef,
				searchConfig: {
					filter: "ISNOTNULL:\"sys:node-uuid\" AND NOT (@lecm\\-d8n:delegation\\-opts\\-status:\"NOT_SET\")"
				}
			}
		});
	});

	function onShowOnlyConfiguredChanged () {
		var cbShowOnlyConfigured = YAHOO.util.Dom.get("cbShowOnlyConfigured");
		var obj = {
			datagridMeta: datagrid.datagridMeta
		};
		if (cbShowOnlyConfigured.checked) {
			obj.datagridMeta.searchConfig.filter = "ISNOTNULL:\"sys:node-uuid\" AND NOT (@lecm\\-d8n:delegation\\-opts\\-status:\"NOT_SET\")"
		} else {
			obj.datagridMeta.searchConfig.filter = ""
		}
		YAHOO.Bubbling.fire ("activeGridChanged", obj);
	};
//]]>
</script>

<div align="right" style="padding-top: 0.5em;">
	<input type="checkbox" class="formsCheckBox" id="cbShowOnlyConfigured" onChange="onShowOnlyConfiguredChanged()" checked>
	<label class="checkbox" for="cbShowOnlyConfigured">Отображать только настроенные</label>
</div>
<@grid.datagrid id showViewForm/>
