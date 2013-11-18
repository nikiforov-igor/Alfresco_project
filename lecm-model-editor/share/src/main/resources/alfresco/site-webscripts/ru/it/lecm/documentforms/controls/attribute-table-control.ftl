<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign aDateTime = .now>
<#assign datagridId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign toolbarId = fieldHtmlId + "-toolbar-" + aDateTime?iso_utc>

<#if field.control.params.bubblingLabel??>
	<#assign bubblingId = field.control.params.bubblingLabel + aDateTime?iso_utc/>
<#else>
	<#assign bubblingId = "custom"/>
</#if>

<script type="text/javascript">//<![CDATA[
	function initControl() {
		new LogicECM.module.FormsEditor.AttributesTable("${datagridId}-body").setMessages(${messages}).setOptions({
			bubblingLabel: "${bubblingId}",
			itemNodeRef: "${form.arguments.itemId}"
		});

		new LogicECM.module.FormsEditor.AttributesTableToolbar("${toolbarId}").setMessages(${messages}).setOptions({
			bubblingLabel: "${bubblingId}"
		});
	}
	YAHOO.util.Event.onDOMReady(initControl);
//]]></script>

<div class="form-field with-grid">
	<div id="${toolbarId}" class="subscribe">
		<@comp.baseToolbar toolbarId true false false>
			<div class="new-row">
		        <span id="${toolbarId}-newRowButton" class="yui-button yui-push-button">
		           <span class="first-child">
		              <button type="button" title="${msg("label.form.attributeTable.fields.add.title")}">${msg("label.form.attributeTable.fields.add.title")}</button>
		           </span>
		        </span>
			</div>
		</@comp.baseToolbar>
	</div>

	<div id="${datagridId}" class="subscribe">
		<@grid.datagrid datagridId false>
			<script type="text/javascript">//<![CDATA[
			(function () {
				YAHOO.util.Event.onDOMReady(function (){
					new LogicECM.module.Base.DataGrid('${datagridId}').setOptions({
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
						showCheckboxColumn: false
					}).setMessages(${messages});
				});

			})();
			//]]></script>
		</@grid.datagrid>
	</div>
</div>
