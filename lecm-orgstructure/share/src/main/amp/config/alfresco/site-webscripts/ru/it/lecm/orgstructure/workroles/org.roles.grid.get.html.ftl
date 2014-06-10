<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />
<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>

<!-- Historic Properties Viewer -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/versions.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" />

<!-- Tree -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-orgstructure/orgstructure-tree.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-tree.js"></@script>


<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="orgstructure-dictionary-grid">
	<div id="yui-main-2">
		<div class="yui-b datagrid-content" id="alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true showArchiveCheckBox=true>
			<script type="text/javascript">//<![CDATA[
			(function() {
				function createDatagrid() {
	
	                // Переопределяем метод onActionDelete. Добавляем проверки
	                LogicECM.module.Base.DataGrid.prototype.onActionDelete =
	                    function DataGridActions_onActionDelete(p_items, owner, actionsConfig, fnDeleteComplete) {
	                        var me = this;
	                        var orgRoleToDelete = YAHOO.lang.isArray(p_items) ? p_items[0] : p_items;
	
	                        // Проверим назначены ли на роль сотрудники
	                        var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getOrgRoleEmployees?nodeRef=" + orgRoleToDelete.nodeRef;
	                        var callback = {
	                            success:function (oResponse) {
	                                var oResults = eval("(" + oResponse.responseText + ")");
	                                if (oResults && oResults.length > 0) {
	                                    var employees = [];
	                                    var i;
	                                    for (i in oResults) {
	                                        employees.push(oResults[i].shortName);
	                                    }
	
	                                    var employeesStr = employees.join(", ");
	                                    Alfresco.util.PopupManager.displayMessage(
	                                        {
	                                            text:me.msg("message.delete.org.role.failure.employees.assigned", employeesStr)
	                                        });
	                                } else {
	                                    me.onDelete(p_items, owner, actionsConfig, fnDeleteComplete, null);
	                                }
	                            },
	                            failure:function (oResponse) {
	                                Alfresco.util.PopupManager.displayMessage(
	                                    {
	                                        text:me.msg("message.delete.org.role.error")
	                                    });
	                            },
	                            argument:{
	                            }
	                        };
	                        YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
	                    };
	
					new LogicECM.module.Base.DataGrid('${id}').setOptions(
							{
								usePagination:true,
								showExtendSearchBlock:true,
	                            showActionColumn: LogicECM.module.OrgStructure.IS_ENGINEER ? true : false,
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
					createDatagrid();
				}
	
				YAHOO.util.Event.onDOMReady(init);
			})();
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
