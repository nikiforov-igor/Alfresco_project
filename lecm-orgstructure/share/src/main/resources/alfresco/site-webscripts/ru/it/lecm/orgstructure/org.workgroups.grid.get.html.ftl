<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div id="orgstructure-workgroup-grid">
	<div id="yui-main-3">
		<div id="${id}-alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=false>
			<script type="text/javascript">//<![CDATA[
			function createWorkgroupDatagrid() {
				new LogicECM.module.Orgstructure.WorkGroupDataGrid('${id}').setOptions(
						{
							bubblingLabel:"${bubblingLabel!"workGroup"}",
							usePagination:true,
							showExtendSearchBlock:false,
							actions: [
								{
									type:"action-link-${bubblingLabel!"workGroup"}",
									id:"onActionEdit",
									permission:"edit",
									label:"${msg("actions.edit")}"
								},
								{
									type:"action-link-${bubblingLabel!"workGroup"}",
									id:"onActionVersion",
									permission:"edit",
									label:"${msg("actions.version")}"
								},
								{
									type:"action-link-${bubblingLabel!"workGroup"}",
									id:"onActionDelete",
									permission:"delete",
									label:"${msg("actions.delete-row")}"
								}
							],
							showCheckboxColumn: false,
							attributeForShow:"lecm-orgstr:element-short-name"
						}).setMessages(${messages});
			}

			function init() {
                createWorkgroupDatagrid();
			}

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
