<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="orgstructure-employees-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
			<!-- include base datagrid markup-->
		<@grid.datagrid id true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {
				var datagrid = new LogicECM.module.Base.DataGrid ("${id}");
				datagrid.setOptions ({
					usePagination:true,
					showExtendSearchBlock:true,
					showCheckboxColumn: false,
					showActionColumn: LogicECM.module.OrgStructure.IS_ENGINEER ? true : false,
					editForm: "configureBusinessRole",
					attributeForShow: "cm:name",
					actions: [{
						type:"datagrid-action-link-${bubblingLabel!"business-role"}",
						id:"onActionEdit",
						permission:"edit",
						label:"${msg("actions.edit")}"
					}],
					bubblingLabel: "${bubblingLabel!"business-role"}"
				});
				datagrid.setMessages (${messages});

				YAHOO.util.Event.onContentReady ('${id}', function () {
					YAHOO.Bubbling.fire ("activeGridChanged", {
						datagridMeta: {
							itemType: LogicECM.module.OrgStructure.BUSINESS_ROLES_SETTINGS.itemType,
							nodeRef: LogicECM.module.OrgStructure.BUSINESS_ROLES_SETTINGS.nodeRef,
							actionsConfig:{
								fullDelete:LogicECM.module.OrgStructure.BUSINESS_ROLES_SETTINGS.fullDelete
							},
							searchConfig: {
								filter: "@lecm\\-orgstr:business\\-role\\-is\\-dynamic:false"
							}
						},
						bubblingLabel: "${bubblingLabel!"business-role"}"
					});
				});
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
