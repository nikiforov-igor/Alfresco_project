<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="orgstructure-employees-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true showArchiveCheckBox=true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {
				var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							usePagination:true,
							useDynamicPagination:true,
							showExtendSearchBlock:true,
                            showActionColumn: LogicECM.module.OrgStructure.IS_ENGINEER ? true : false,
							actions: [
								{
									type:"datagrid-action-link-${bubblingLabel!"employee"}",
									id:"onActionEdit",
									permission:"edit",
									label:"${msg("actions.edit")}"
								},
								{
									type:"datagrid-action-link-${bubblingLabel!"employee"}",
									id:"onActionVersion",
									permission:"edit",
									label:"${msg("actions.version")}"
								},
								{
									type:"datagrid-action-link-${bubblingLabel!"employee"}",
									id:"onActionDelete",
									permission:"delete",
									label:"${msg("actions.delete-row")}",
									evaluator: function (rowData) {
                                        var itemData = rowData.itemData;
                                        var isActive = itemData["prop_lecm-dic_active"] == undefined || itemData["prop_lecm-dic_active"].value == true;
                                        return isActive && (itemData["prop_lecm-orgstr_employee-main-position"] == undefined ||
		                                        itemData["prop_lecm-orgstr_employee-main-position"].value.length == 0);
                                    }
								}
							],
							bubblingLabel: "${bubblingLabel!"employee"}",
							showCheckboxColumn: false,
							attributeForShow:"lecm-orgstr:employee-last-name"
						}).setMessages(${messages});

                YAHOO.util.Event.onContentReady ('${id}', function () {
                    YAHOO.Bubbling.fire ("activeGridChanged", {
                        datagridMeta: {
                            itemType: LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS.itemType,
                            nodeRef: LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS.nodeRef,
                            actionsConfig:{
                                fullDelete:LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS.fullDelete
                            }
                        },
                        bubblingLabel: "${bubblingLabel!"employee"}"
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
