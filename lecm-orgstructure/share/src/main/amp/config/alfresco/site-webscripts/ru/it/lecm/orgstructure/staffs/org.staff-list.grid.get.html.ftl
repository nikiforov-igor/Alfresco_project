<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/orgstructure/components/orgstructure-tree.ftl" as orgTree/>
<#assign id = args.htmlid>
<#assign showSearchBlock = true/>
        <!-- include base datagrid markup-->
		<@grid.datagrid id false>
			<script type="text/javascript">//<![CDATA[
            function createDatagrid() {
					var datagrid = new LogicECM.module.Orgstructure.StaffListDataGrid('${id}').setOptions(
							{
								usePagination:true,
								showExtendSearchBlock:${showSearchBlock?string},
//                                showActionColumn: LogicECM.module.OrgStructure.IS_ENGINEER ? true : false,
                                showActionColumn: true,
								actions: [
									{
										type:"datagrid-action-link-${bubblingLabel!"staff-list"}",
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
										type:"datagrid-action-link-${bubblingLabel!"staff-list"}",
										id:"onActionEmployeeDelete",
										permission:"edit",
										label:"${msg("actions.deleteEmployee")}",
										evaluator:function (rowData) {
                                            var itemData = rowData.itemData;
                                            return itemData["assoc_lecm-orgstr_element-member-employee-assoc"] != undefined &&
		                                            itemData["assoc_lecm-orgstr_element-member-employee-assoc"].value.length > 0;
                                        }
									},
									{
										type:"datagrid-action-link-${bubblingLabel!"staff-list"}",
										id:"onActionEdit",
										permission:"edit",
										label:"${msg("actions.edit")}"
									},
									{
										type:"datagrid-action-link-${bubblingLabel!"staff-list"}",
										id:"onActionDelete",
										permission:"delete",
										label:"${msg("actions.delete-row")}",
										evaluator: function (rowData) {
                                            var itemData = rowData.itemData;
                                            return itemData["assoc_lecm-orgstr_element-member-employee-assoc"] == undefined ||
		                                            itemData["assoc_lecm-orgstr_element-member-employee-assoc"].value.length == 0;
                                        }
									},
									{
										type:"datagrid-action-link-${bubblingLabel!"staff-list"}",
										id:"onActionMakeBoss",
										permission:"edit",
										label:"${msg("actions.makeBoss")}",
										evaluator:function (rowData) {
                                            var itemData = rowData.itemData;
                                            return itemData["prop_lecm-orgstr_staff-list-is-boss"].value == false;
                                        }
									}
								],
								bubblingLabel: "${bubblingLabel!"staff-list"}",
								showCheckboxColumn: false,
								attributeForShow:"lecm-orgstr:element-member-position-assoc"
							}).setMessages(${messages});
				}

                function init() {
                    LogicECM.module.Base.Util.loadResources([
                        'components/form/number-range.js',
                        'scripts/lecm-base/components/versions.js',
                        'scripts/lecm-orgstructure/employee-validations.js',
                        'modules/simple-dialog.js',
                        'scripts/lecm-base/components/advsearch.js',
                        'scripts/lecm-base/components/lecm-datagrid.js',
                        'scripts/lecm-orgstructure/stafflist-datagrid.js'
                    ], [
                        'components/search/search.css',
                        'modules/document-details/historic-properties-viewer.css',
                        'css/lecm-orgstructure/orgstructure-employee-absence.css'
                    ], createDatagrid);
                }

                YAHOO.util.Event.onDOMReady(init);

			//]]></script>
		</@grid.datagrid>



