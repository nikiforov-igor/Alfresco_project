<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign datagridId = "controlsDatagrid-" + args.htmlid/>
<#assign datagridBubblingLabel = bubblingLabel + "-" + args.htmlid/>

<div id="${datagridId}">
<@grid.datagrid id=datagridId/>
</div>
<@inlineScript group="lecm-controls-editor">
(function() {
	var Bubbling = YAHOO.Bubbling,
		datagrid = new LogicECM.module.Base.DataGrid("${datagridId}");
	datagrid.setMessages(${messages});
	//TODO: добавить action "по-умолчанию", который позволит только один контрол отметить галкой "по-умолчанию"
	datagrid.setOptions({
		allowCreate: false,
		showActionColumn: true,
		overrideSortingWith: false,
		showExtendSearchBlock: false,
		showCheckboxColumn: false,
		bubblingLabel: "${datagridBubblingLabel}",
		attributeForShow: "cm:title",
		actions: [{
			type: "datagrid-action-link-${datagridBubblingLabel}",
			id: "onActionEdit",
			permission: "edit",
			label: "${msg("actions.edit")}"
		},{
			type: "datagrid-action-link-${datagridBubblingLabel}",
			id: "onActionDelete",
			permission: "delete",
			label: "${msg("actions.delete-row")}"
		}],
		datagridMeta: {
			sort: "cm:title|true",
			itemType: "lecm-controls-editor:control",
			nodeRef: "${context.page.properties["typeRoot"]}",
			useChildQuery: false,
			actionsConfig: {
				fullDelete: true,
				trash: false
			}
		}
	});

	Bubbling.on("initDatagrid", drawDatagrid);

	function drawDatagrid(layer, args) {
		var bubblingLabel = '${datagridBubblingLabel}';
		if (bubblingLabel == args[1].datagrid.options.bubblingLabel) {
			args[1].datagrid.draw();
			Bubbling.unsubscribe(layer, drawDatagrid);
		}
	}
})();
</@>
