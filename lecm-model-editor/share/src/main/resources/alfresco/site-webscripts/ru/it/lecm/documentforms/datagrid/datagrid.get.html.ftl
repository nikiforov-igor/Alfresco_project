<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="document-forms-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true>
			<script type="text/javascript">//<![CDATA[
			function init() {
				new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							usePagination: false,
							actions: [
								{
									type: "datagrid-action-link-${bubblingLabel!''}",
									id: "onActionEdit",
									permission: "edit",
									label: "${msg("actions.edit")}"
								},
								{
									type: "datagrid-action-link-${bubblingLabel!''}",
									id: "onActionDelete",
									permission: "delete",
									label: "${msg("actions.delete-row")}"
								}
							],
							bubblingLabel: "${bubblingLabel!''}",
							attributeForShow: "cm:name"
						}).setMessages(${messages});
			}

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
