<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>

<#if field.control.params.bubblingLabel??>
	<#assign bubblingId = field.control.params.bubblingLabel/>
<#else>
	<#assign bubblingId = "custom"/>
</#if>

<script type="text/javascript">//<![CDATA[
	function initControl() {
		new LogicECM.module.FormsEditor.AttributesTable("${containerId}-body").setMessages(${messages}).setOptions({
			bubblingLabel: "${bubblingId}",
			itemNodeRef: "${form.arguments.itemId}"
		});
	}
	YAHOO.util.Event.onDOMReady(initControl);
//]]></script>

<div class="form-field with-grid" id="${containerId}">
<@grid.datagrid containerId false>
	<script type="text/javascript">//<![CDATA[
	(function () {
		YAHOO.util.Event.onDOMReady(function (){
			new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
				usePagination: false,
				actions: [
						{
							type: "datagrid-action-link-${bubblingId}",
							id: "onActionEdit",
							permission: "edit",
							label: "${msg("actions.edit")}"
						},
						{
							type: "datagrid-action-link-${bubblingId}",
							id: "onActionDelete",
							permission: "delete",
							label: "${msg("actions.delete-row")}"
						}
				],
				bubblingLabel: "${bubblingId}",
				showCheckboxColumn: false,
				allowCreate: true
			}).setMessages(${messages});
		});

	})();
	//]]></script>
</@grid.datagrid>
</div>
