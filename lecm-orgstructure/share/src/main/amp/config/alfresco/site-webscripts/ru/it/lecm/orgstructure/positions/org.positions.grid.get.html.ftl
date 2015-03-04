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
	                        var me = this;
	                        var positionToDelete = YAHOO.lang.isArray(p_items) ? p_items[0] : p_items;

	                        // Проверим назначены ли на должность сотрудники
	                        var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getPositionEmployees?nodeRef=" + positionToDelete.nodeRef;
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
	                                                text:me.msg("message.delete.position.failure.employees.assigned", employeesStr)
	                                            });
	                                } else {
	                                    me.onDelete(p_items, owner, actionsConfig, fnDeleteComplete, null);
	                                }
	                            },
	                            failure:function (oResponse) {
	                                Alfresco.util.PopupManager.displayMessage(
	                                        {
	                                            text:me.msg("message.delete.position.error")
	                                        });
	                            },
	                            argument:{
	                            }
	                        };
	                        YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
	                    };

					var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions(
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
								bubblingLabel: "${bubblingLabel!"staffPosition"}",
								showCheckboxColumn: false,
								attributeForShow:"cm:name"
							}).setMessages(${messages});

	                YAHOO.util.Event.onContentReady ('${id}', function () {
	                    YAHOO.Bubbling.fire ("activeGridChanged", {
	                        datagridMeta: {
                                useFilterByOrg: false,
	                            itemType: LogicECM.module.OrgStructure.POSITIONS_SETTINGS.itemType,
	                            nodeRef: LogicECM.module.OrgStructure.POSITIONS_SETTINGS.nodeRef,
	                            actionsConfig:{
	                                fullDelete:LogicECM.module.OrgStructure.POSITIONS_SETTINGS.fullDelete
	                            }
	                        },
	                        bubblingLabel: "${bubblingLabel!"staffPosition"}"
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
                        'scripts/lecm-orgstructure/orgstructure-tree.js'
                    ], [
                        'components/search/search.css',
                        'modules/document-details/historic-properties-viewer.css',
                        'yui/treeview/assets/skins/sam/treeview.css',
                        'css/lecm-orgstructure/orgstructure-tree.css'
                    ], createDatagrid);
                }

				YAHOO.util.Event.onDOMReady(init);
			})();
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
