<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-validation.js"/>
<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"/>
<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />

<!-- Historic Properties Viewer -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/versions.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" />

<!-- Tree -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-orgstructure/orgstructure-tree.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-tree.js"></@script>


<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="orgstructure-employees-grid">
	<div id="yui-main-2">
		<div class="yui-b datagrid-content" id="alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true showArchiveCheckBox=true>
			<script type="text/javascript">//<![CDATA[
			(function () {
				function createDatagrid() {
	
	                YAHOO.lang.augmentObject(LogicECM.module.Base.DataGrid.prototype, {
	
			                	makeJquerySyncRequestForAbsence : function _makeJquerySyncRequestForAbsence(url, payload, showMessage, comment ){
			                        var result = {};
	
			                        result.hasNoActiveAbsences = false;
	
			                        // Yahoo UI не умеет синхронный (блокирующий) AJAX. Придется использовать jQuery
			                        jQuery.ajax({
			                            url: Alfresco.constants.PROXY_URI_RELATIVE + url,
			                            type: "POST",
			                            timeout: 30000, // 30 секунд таймаута хватит всем!
			                            async: false, // ничего не делаем, пока не отработал запром
			                            dataType: "json",
			                            contentType: "application/json",
			                            data: YAHOO.lang.JSON.stringify(payload), // jQuery странно кодирует данные. пусть YUI эаймеся преобразованием в JSON
			                            processData: false, // данные не трогать, не кодировать вообще
			                            success: function (response, textStatus, jqXHR) {
			                                if (response && response.hasNoActiveAbsences) {
			                                    result.hasNoActiveAbsences = true;
			                                } else {
			                                    result.hasNoActiveAbsences = false;
			                                    result.reason = response.reason;
			                                }
			                            },
			                            error: function(jqXHR, textStatus, errorThrown) {
			                                result.hasNoActiveAbsences = false;
			                                result.errorText = textStatus;
			                            }
			                        });
	
			                        if (showMessage){
			                            if (result.errorText){
			                                Alfresco.util.PopupManager.displayMessage(
			                                        {
			                                            text:result.errorText
			                                        });
			                            }else{
			                                if ( !result.hasNoActiveAbsences && result.reason){
			                                    Alfresco.util.PopupManager.displayMessage(
			                                            {
			                                                text:  comment
			                                            });
			                                }
			                            }
			                        }
	
			                        return result;
			                    },
	                            //Действия по умолчанию. В конкретных реализациях ДатаГрида эти методы при необходимости следует переопределять
	                            /**
	                             * Delete item(s).
	                             *
	                             * @method onActionDelete
	                             * @param p_items {Object | Array} Object literal representing the Data Item to be actioned, or an Array thereof
	                             * @param owner {Object} не используется Dom-объект
	                             * @param actionsConfig {Object} Объект с настройками для экшена
	                             * @param fnDeleteComplete {Object} CallBack, который вызовется после завершения удаления
	                             */
	                            onActionDelete:function DataGridActions_onActionDelete(p_items, owner, actionsConfig, fnDeleteComplete) {
	                                this.checkBeforeDeleteAction(p_items, owner, actionsConfig, fnDeleteComplete);
	                            },
	
	                            checkBeforeDeleteAction: function DataGridActions_checkBeforeDelete(p_items, owner, actionsConfig, fnDeleteComplete){
	                                if (this.checkBeforeDeleteSync(p_items)){
	                                    this.onDelete(p_items, owner, actionsConfig, fnDeleteComplete, null);
	                                }
	                            },
	
	                            checkBeforeDeleteSync: function DataGridActions_checkBeforeDeleteSync(p_items){                            	
	                                   
	                                var hasNoActiveAbsences = this.makeJquerySyncRequestForAbsence("lecm/orgstructure/api/employeeHasNoAbsences",
	                                                                                               { nodeRef : p_items.nodeRef },
	                                                                                               true,
	                                                                                               "Сотрудник не может быть уволен т.к. имеет активные отсутсвия"
	                                                                                              );
	                                if (hasNoActiveAbsences && hasNoActiveAbsences.hasNoActiveAbsences){
	                                    return true;
	                                }
	                                   
	                                return false;
	                            }
	
	                        },
	                    true
	                );
	
					var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions(
							{
								usePagination:true,
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
	                                        var isActive = this.isActiveItem(itemData);
	                                        return isActive && (itemData["prop_lecm-orgstr_employee-main-position"] == undefined ||
			                                        itemData["prop_lecm-orgstr_employee-main-position"].value.length == 0);
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
			})();
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
