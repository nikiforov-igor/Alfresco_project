<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="orgstructure-dictionary-grid">
	<div id="yui-main-2">
		<div class="yui-b datagrid-content" id="alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=false showArchiveCheckBox=true>
			<script type="text/javascript">//<![CDATA[
			(function() {
				function createDatagrid() {

	                // Переопределяем метод onActionDelete. Добавляем проверки
	                LogicECM.module.Base.DataGrid.prototype.onActionDelete =
	                    function DataGridActions_onActionDelete(p_items, owner, actionsConfig, fnDeleteComplete) {
	                        var orgRoleToDelete = YAHOO.lang.isArray(p_items) ? p_items[0] : p_items;

	                        // Проверим назначены ли на роль сотрудники
	                        var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getOrgRoleEmployees?nodeRef=" + orgRoleToDelete.nodeRef;
	                        Alfresco.util.Ajax.jsonGet({
                                url: sUrl,
                                successCallback: {
                                    fn: function (response) {
	                                    var oResults = response.json;
	                                    if (oResults && oResults.length > 0) {
	                                        var employees = [], i;
	                                        for (i in oResults) {
	                                            employees.push(oResults[i].shortName);
	                                        }

	                                        var employeesStr = employees.join(", ");
	                                        Alfresco.util.PopupManager.displayMessage({
	                                            text: this.msg("message.delete.org.role.failure.employees.assigned", employeesStr)
	                                        });
	                                    } else {
	                                        this.onDelete(p_items, owner, actionsConfig, fnDeleteComplete, null);
	                                    }
	                                },
                                    scope: this
                                },
                                failureMessage: this.msg("message.delete.org.role.error")
                            });
	                    };

					new LogicECM.module.Base.DataGrid('${id}').setOptions(
							{
								usePagination:true,
								showExtendSearchBlock:true,
//	                            showActionColumn: LogicECM.module.OrgStructure.IS_ENGINEER ? true : false,
	                            showActionColumn: true,
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
										label:"${msg("actions.delete-row")}",
										evaluator: function (rowData) {
											return this.isActiveItem(rowData.itemData);
										}
									},
									{
										type:"datagrid-action-link-${bubblingLabel!"employee"}",
										id:"onActionRestore",
										permission:"delete",
										label:"${msg("actions.restore-row")}",
										evaluator: function (rowData) {
											return !this.isActiveItem(rowData.itemData);
										}
									}
								],
								bubblingLabel: "${bubblingLabel!"roles"}",
								showCheckboxColumn: false,
								attributeForShow:"cm:name"
							}).setMessages(${messages});

	                YAHOO.util.Event.onContentReady ('${id}', function () {
	                    YAHOO.Bubbling.fire ("activeGridChanged", {
	                        datagridMeta: {
                                useFilterByOrg: false,
	                            itemType: LogicECM.module.OrgStructure.WORK_ROLES_SETTINGS.itemType,
	                            nodeRef: LogicECM.module.OrgStructure.WORK_ROLES_SETTINGS.nodeRef,
	                            actionsConfig:{
	                                fullDelete:LogicECM.module.OrgStructure.WORK_ROLES_SETTINGS.fullDelete
	                            }
	                        },
	                        bubblingLabel: "${bubblingLabel!"roles"}"
	                    });
	                });
				}

				function init() {
                    LogicECM.module.Base.Util.loadResources([
                        'modules/simple-dialog.js',
                        'scripts/lecm-base/components/advsearch.js',
                        'scripts/lecm-base/components/lecm-datagrid.js',
                        'components/form/date-range.js',
                        'components/form/number-range.js',
                        'scripts/lecm-base/components/versions.js',
                        'scripts/lecm-orgstructure/orgstructure-tree.js',
                        'scripts/lecm-orgstructure/orgstructure-utils.js'
                    ], [
                        'components/search/search.css',
                        'modules/document-details/historic-properties-viewer.css',
                        'yui/treeview/assets/skins/sam/treeview.css',
                        'css/lecm-orgstructure/orgstructure-tree.css'
                    ], createDatagrid);				}

				YAHOO.util.Event.onDOMReady(init);
			})();
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
