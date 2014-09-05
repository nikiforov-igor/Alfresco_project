<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div  id="orgstructure-workforces-grid">
	<div id="yui-main-2">
		<div id="${id}-alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=false>
			<script type="text/javascript">//<![CDATA[
			function createWorkforceDatagrid() {
				var datagrid = new LogicECM.module.Orgstructure.WorkForceDataGrid('${id}').setOptions(
						{
							bubblingLabel: "${bubblingLabel!"workForce"}",
							usePagination:true,
							showExtendSearchBlock:false,
							actions: [
								{
									type:"datagrid-action-link-${bubblingLabel!"workForce"}",
									id:"onActionEmployeeAdd",
									permission:"edit",
									label:"${msg("actions.addEmployee")}",
									evaluator: function (rowData) {
                                        var itemData = rowData.itemData;
                                        return itemData["assoc_lecm-orgstr_element-member-employee-assoc"] == undefined ||
		                                        itemData["assoc_lecm-orgstr_element-member-employee-assoc"].value.length == 0;
                                    }
								},
								{
									type:"datagrid-action-link-${bubblingLabel!"workForce"}",
									id:"onActionEmployeeDelete",
									permission:"edit",
									label:"${msg("actions.deleteEmployee")}",
									evaluator: function (rowData) {
                                        var itemData = rowData.itemData;
                                        return itemData["assoc_lecm-orgstr_element-member-employee-assoc"] != undefined &&
		                                        itemData["assoc_lecm-orgstr_element-member-employee-assoc"].value.length > 0;
                                    }
								},
								{
									type:"datagrid-action-link-${bubblingLabel!"workForce"}",
									id:"onActionEdit",
									permission:"edit",
									label:"${msg("actions.edit")}"
								},
								{
									type:"datagrid-action-link-${bubblingLabel!"workForce"}",
									id:"onActionDelete",
									permission:"delete",
									label:"${msg("actions.delete-row")}",
									evaluator: function (rowData) {
                                        var itemData = rowData.itemData;
                                        return itemData["assoc_lecm-orgstr_element-member-employee-assoc"] == undefined ||
		                                        itemData["assoc_lecm-orgstr_element-member-employee-assoc"].value.length == 0;
                                    }
								}
							],
                            datagridMeta: {
                                itemType: "lecm-orgstr:workforce",
                                nodeRef: "NOT_LOAD",
                                actionsConfig: {
                                    fullDelete:false
                                }
                            },
							showCheckboxColumn: false,
                            showActionColumn: LogicECM.module.OrgStructure.IS_ENGINEER ? true : false,
							attributeForShow:"lecm-orgstr:element-member-position-assoc"
						}).setMessages(${messages});
                datagrid.draw();
			}

            function init() {
                LogicECM.module.Base.Util.loadResources([
                    'jquery/jquery-1.6.2.js',
                    'components/form/date-range.js',
                    'components/form/number-range.js',
                    'modules/simple-dialog.js',
                    'scripts/lecm-base/components/advsearch.js',
                    'scripts/lecm-base/components/lecm-datagrid.js',
                    'scripts/lecm-orgstructure/workgroup-datagrid.js',
                    'scripts/lecm-orgstructure/workforce-datagrid.js',
                    'scripts/lecm-base/components/versions.js'
                ], [
                    'components/search/search.css',
                    'modules/document-details/historic-properties-viewer.css',
                    'css/lecm-orgstructure/orgstructure-work-groups.css'
                ], createWorkforceDatagrid);
            }

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
