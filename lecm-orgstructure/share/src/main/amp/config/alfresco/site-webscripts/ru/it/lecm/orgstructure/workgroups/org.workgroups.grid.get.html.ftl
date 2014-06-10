<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div id="orgstructure-workgroup-grid">
	<div id="yui-main-3">
		<div id="${id}-alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=false>
			<script type="text/javascript">//<![CDATA[
			(function() {
				function createWorkgroupDatagrid() {
					new LogicECM.module.Orgstructure.WorkGroupDataGrid('${id}').setOptions(
							{
								bubblingLabel:"${bubblingLabel!"workGroup"}",
								usePagination:true,
								showExtendSearchBlock:false,
	                            showActionColumn: LogicECM.module.OrgStructure.IS_ENGINEER ? true : false,
								actions: [
									{
										type:"datagrid-action-link-${bubblingLabel!"workGroup"}",
										id:"onActionEdit",
										permission:"edit",
										label:"${msg("actions.edit")}"
									},
									{
										type:"datagrid-action-link-${bubblingLabel!"workGroup"}",
										id:"onActionVersion",
										permission:"edit",
										label:"${msg("actions.version")}"
									},
									{
										type:"datagrid-action-link-${bubblingLabel!"workGroup"}",
										id:"onActionDelete",
										permission:"delete",
										label:"${msg("actions.delete-row")}"
									}
								],
								showCheckboxColumn: false,
								attributeForShow:"lecm-orgstr:element-short-name"
							}).setMessages(${messages});
	
	                YAHOO.util.Event.onContentReady ('${id}', function () {
	                    YAHOO.Bubbling.fire ("activeGridChanged", {
	                        datagridMeta: {
	                            itemType: LogicECM.module.OrgStructure.WORK_GROUPS_SETTINGS.itemType,
	                            nodeRef: LogicECM.module.OrgStructure.WORK_GROUPS_SETTINGS.nodeRef,
	                            actionsConfig:{
	                                fullDelete:LogicECM.module.OrgStructure.WORK_GROUPS_SETTINGS.fullDelete
	                            }
	                        },
	                        bubblingLabel: "${bubblingLabel!"workGroup"}"
	                    });
	                });
				}
	
				function init() {
	                createWorkgroupDatagrid();
				}
	
				YAHOO.util.Event.onDOMReady(init);
			})();
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
