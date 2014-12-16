<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign datagridId = "controlsDatagrid" + args.htmlid/>
<#assign datagridBubblingLabel = datagridId/>

<div id="${datagridId}">
<@grid.datagrid id=datagridId/>
</div>
<@inlineScript group="lecm-controls-editor">
(function() {
	var Bubbling = YAHOO.Bubbling,
		datagrid = new LogicECM.module.Base.DataGrid("${datagridId}");
	datagrid.setMessages(${messages});
	datagrid.setOptions({
		showExtendSearchBlock: false,
		showCheckboxColumn: false,
		bubblingLabel: "${datagridBubblingLabel}",
		attributeForShow: "cm:title",
		datagridMeta: {
			itemType: "lecm-controls-editor:control",
			nodeRef: "${typeRoot}",
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
