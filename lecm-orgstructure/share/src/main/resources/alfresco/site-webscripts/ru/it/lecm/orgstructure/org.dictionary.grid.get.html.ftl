<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="orgstructure-dictionary-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true showArchiveCheckBox=true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {
				new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							usePagination:true,
							showExtendSearchBlock:true,
							actions: [
								{
									type:"datagrid-action-link-${bubblingLabel!"dictionary"}",
									id:"onActionEdit",
									permission:"edit",
									label:"${msg("actions.edit")}"
								},
								{
									type:"datagrid-action-link-${bubblingLabel!"dictionary"}",
									id:"onActionDelete",
									permission:"delete",
									label:"${msg("actions.delete-row")}"
								}
							],
							bubblingLabel: "${bubblingLabel!"dictionary"}",
							showCheckboxColumn: false,
							attributeForShow:"cm:name"
						}).setMessages(${messages});
			}

			function init() {
				createDatagrid();
			}

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
