<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />

<!-- Historic Properties Viewer -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/versions.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" />


<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/employee-has-no-absencesl-validation.js"/>

<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/orgstructure/components/orgstructure-tree.ftl" as orgTree/>
<#assign id = args.htmlid>
<#assign showSearchBlock = true/>


	
                <!-- include base datagrid markup-->
		<@grid.datagrid id>
			<script type="text/javascript">//<![CDATA[
			(function () {
				function init() {
                    LogicECM.module.Base.DataGrid.prototype.onActionEmployeeAdd = function DataGridActions_onActionEmployeeAdd(p_item, owner, actionsConfig, fnCallback) {
                        var me = this;
                        var metaData = {
                            itemType: "lecm-orgstr:employee-link",
                            createFormId: "selectEmployeeWithNoAbsences",
                            nodeRef: p_item.nodeRef,
                            addMessage: this.msg("label.employee-link.add")
                        };

                        var onAddCallback = function (employeeRef) {
                            // Reload the node's metadata
                            YAHOO.Bubbling.fire("datagridRefresh",
                                    {
                                        bubblingLabel: me.options.bubblingLabel
                                    });
                            Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.employee.add.success")
                                    });
                        }.bind(me);

                        this.showCreateDialog(metaData, onAddCallback);
                    };
					LogicECM.module.Base.DataGrid.prototype.onActionEmployeeDelete = function DataGridActions_onActionEmployeeDelete(p_item, owner, actionsConfig, fnDeleteComplete) {
						var me = this;
						var staffRow = p_item;
                        var subnitRow = this.datagridMeta.nodeRef;
						// Подразделение в котором находится сотрудник
						var sUrl =  Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getUnitProperties?nodeRef=" + subnitRow;
						var callback = {
                            success: function(oResponse){
                            var oResults = eval("(" + oResponse.responseText + ")");
	                        var subnitRowName = oResults .fullName;
                                // Получаем для штатного расписания ссылку на сотрудника
                                var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getStaffEmployeeLink?nodeRef=" + staffRow.nodeRef;
                                var callback = {
                                    success:function (oResponse) {
                                        var oResult = eval("(" + oResponse.responseText + ")");
                                        if (oResult) {

                                            var hasNoActiveAbsences = LogicECM.module.Base.DataGrid.prototype.checkEmployeeHasNoActiveAbsences(oResult.employee, "Невозможно снять сотрудника, т.к. оформлено отсутствие \n")

                                            if (hasNoActiveAbsences && !hasNoActiveAbsences.hasNoActiveAbsences){
                                                return;
                                            }

                                            var onPrompt = function (fnAfterPrompt) {
                                                Alfresco.util.PopupManager.displayPrompt(
                                                        {
                                                            title:this.msg("message.employee.position.delete.title"),
                                                            text: this.msg("message.employee.position.delete.prompt",
                                                                    staffRow.itemData["assoc_lecm-orgstr_element-member-employee-assoc"].displayValue,
                                                                    staffRow.itemData["assoc_lecm-orgstr_element-member-position-assoc"].displayValue,
                                                                    subnitRowName),
                                                            buttons:[
                                                                {
                                                                    text:this.msg("button.employee.remove"),
                                                                    handler:function DataGridActions__onActionDelete_delete() {
                                                                        this.destroy();
                                                                        fnAfterPrompt.call(me, [oResult]);
                                                                    }
                                                                },
                                                                {
                                                                    text:this.msg("button.cancel"),
                                                                    handler:function DataGridActions__onActionDelete_cancel() {
                                                                        this.destroy();
                                                                    },
                                                                    isDefault:true
                                                                }
                                                            ]
                                                        });
                                            };

                                            if (("" + oResult.is_primary) == "true") {
                                                var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getEmployeePositions?nodeRef=" + oResult.employee;
                                                var callback = {
                                                    success:function (oResponse) {
                                                        var oResults = eval("(" + oResponse.responseText + ")");
                                                        if (oResults && oResults.length > 1) { // нельзя удалять руководящую должность, пока есть другие должности
                                                            Alfresco.util.PopupManager.displayMessage(
                                                                    {
                                                                        text:me.msg("message.employee.position.delete.failure.primary")
                                                                    });
                                                        } else { // удаляем! вызов метода из грида
                                                            me.onDelete([oResult], owner, {fullDelete:true, trash:false, successMessage: "message.employee.position.delete.success"}, fnDeleteComplete, onPrompt);
                                                        }
                                                    },
                                                    failure:function (oResponse) {
                                                        Alfresco.util.PopupManager.displayMessage(
                                                                {
                                                                    text:me.msg("message.employee.position.delete.failure")
                                                                });
                                                    },
                                                    argument:{
                                                    }
                                                };
                                                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                                            } else {
                                                me.onDelete([oResult], owner, {fullDelete:true, trash:false, successMessage: "message.employee.position.delete.success"}, fnDeleteComplete, onPrompt);
                                            }
                                        } else {
                                            Alfresco.util.PopupManager.displayMessage(
                                                    {
                                                        text:this.msg("message.employee.position.delete.failure")
                                                    });
                                        }
                                    },
                                    failure:function (oResponse) {
                                        Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text:this.msg("message.employee.position.delete.failure")
                                                });
                                    }
                                };
                                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                            },
                            failure:function (oResponse) {
                                Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text:me.msg("message.employee.position.delete.failure.primary")
                                        });
                            }
						}
                        YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
					};

                    LogicECM.module.Base.DataGrid.prototype.makeJquerySyncRequestForAbsence = function _makeJquerySyncRequestForAbsence(url, payload, showMessage, comment ){
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
                                                text:  comment + result.reason
                                            });
                                }
                            }
                        }

                        return result;
                    };


                    /**
                     * Синхронная проверка того, что сотрудник не оформлял отсутствие
                     *
                     * @param {Object} employeeNodeRef - NodeRef проверяемого сотрудника
                     * @returns {Object} hasNoActiveAbsences = true - если на данный момент на сотрудника не оформлено отсутствие. Иначе false
                     * @private
                     */
                    LogicECM.module.Base.DataGrid.prototype.checkEmployeeHasNoActiveAbsences = function _сheckEmployeeHasNoActiveAbsences (employeeNodeRef, comment){

                        var result = LogicECM.module.Base.DataGrid.prototype.makeJquerySyncRequestForAbsence(
                                                                                                            "lecm/orgstructure/api/employeeHasNoAbsences",
                                                                                                            { nodeRef : employeeNodeRef },
                                                                                                            true,
                                                                                                            comment);
                        return result;
                    };

                    LogicECM.module.Base.DataGrid.prototype.checkMakeBossHasNoActiveAbsences = function _сheckMakeBossHasNoActiveAbsences (staffNodeRef, comment){

                        var result = LogicECM.module.Base.DataGrid.prototype.makeJquerySyncRequestForAbsence(
                                                                                                            "lecm/orgstructure/api/makeBossHasNoAbsences",
                                                                                                            { nodeRef : staffNodeRef },
                                                                                                            true,
                                                                                                            comment);
                        return result;
                    };

					LogicECM.module.Base.DataGrid.prototype.onActionMakeBoss = function DataGridActions_onActionMakeBoss(p_item, owner, actionsConfig, fnCallback) {
						var me = this;
						var staffRow = p_item;



						// Получаем для штатного расписания ссылку на сотрудника
						var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getStaffEmployeeLink?nodeRef=" + staffRow.nodeRef;
						var callback = {
							success:function (oResponse) {
								var oResult = eval("(" + oResponse.responseText + ")");
								if (oResult) {
									Alfresco.util.PopupManager.displayPrompt(
											{
												title:me.msg("message.position.boss.title"),
												text: me.msg("message.position.boss.prompt",
														staffRow.itemData["assoc_lecm-orgstr_element-member-position-assoc"].displayValue),
												buttons:[
													{
														text:me.msg("button.position.makeBoss"),
														handler:function DataGridActions__onActionMakeBoss_make() {
															this.destroy();

                                                            var hasNoActiveAbsences = LogicECM.module.Base.DataGrid.prototype.checkMakeBossHasNoActiveAbsences(
                                                                    staffRow.nodeRef /*oResult.employee*/,
                                                                    "Невозможно назначить руководящую позицию из-за отсутствия \n");

                                                            if (!(hasNoActiveAbsences && hasNoActiveAbsences.hasNoActiveAbsences)){
                                                                return;
                                                            }

															var onSuccess = function DataGrid_onActionMakeBoss_makeBossConfirmed(response) {
																YAHOO.Bubbling.fire("datagridRefresh",
																		{
																			bubblingLabel:me.options.bubblingLabel
																		});
																Alfresco.util.PopupManager.displayMessage(
																		{
																			text:me.msg("message.position.boss.success")
																		});
															};
															var onFailure = function DataGrid_onActionEmployeeAdd_onFailure(response) {
																Alfresco.util.PopupManager.displayMessage(
																		{
																			text:me.msg("message.position.boss.failure")
																		});
															};
															Alfresco.util.Ajax.jsonRequest(
																	{
																		url:Alfresco.constants.PROXY_URI + "/lecm/orgstructure/action/makeBoss",
																		method:"POST",
																		dataObj:{
																			nodeRef:staffRow.nodeRef
																		},
																		successCallback:{
																			fn:onSuccess,
																			scope:this
																		},
																		failureCallback:{
																			fn:onFailure,
																			scope:this
																		}
																	});
														}
													},
													{
														text:me.msg("button.cancel"),
														handler:function DataGridActions__onActionDelete_cancel() {
															this.destroy();
														},
														isDefault:true
													}
												]
											});
								} else {
									Alfresco.util.PopupManager.displayMessage(
											{
												text:this.msg("message.position.boss.failure")
											});
								}
							},
							failure:function (oResponse) {
								Alfresco.util.PopupManager.displayMessage(
										{
											text:this.msg("message.position.boss.failure")
										});
							}
						};
						YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
					};
					// Переопределяем метод onActionDelete. Добавляем проверки
					LogicECM.module.Base.DataGrid.prototype.onActionDelete = function DataGridActions_onActionDelete(p_items, owner, actionsConfig, fnDeleteComplete) {
						var me = this;
						var	items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];
						var deletedUnit = items[0]; // для штатного расписания одновременно удалить можно ТОЛЬКО ОДНУ должность
						if (deletedUnit.itemData["prop_lecm-orgstr_staff-list-is-boss"].value == false) {
							this.onDelete(p_items, owner, actionsConfig, fnDeleteComplete, null);
						} else {
							//Получаем подразделение сотрудника
							var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getStaffPositionUnit?nodeRef=" + deletedUnit.nodeRef;
							var callback = {
								success:function (oResponse) {
									var oResults = eval("(" + oResponse.responseText + ")");
									if (oResults && oResults.nodeRef) {
										//Получаем все должности подразделения
										var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getUnitStaffPositions?nodeRef=" + oResults.nodeRef;
										var callback = {
											success:function (oResponse) {
												var oResults = eval("(" + oResponse.responseText + ")");
												if (oResults && oResults.length > 1) { // нельзя удалять руководящую должность, пока есть другие должности
													Alfresco.util.PopupManager.displayMessage(
															{
																text:me.msg("message.delete.staff-lest.failure.boss")
															});
												} else { // удаляем! вызов метода из грида
													me.onDelete(p_items, owner, actionsConfig, fnDeleteComplete, null);
												}
											},
											failure:function (oResponse) {
												Alfresco.util.PopupManager.displayMessage(
														{
															text:me.msg("message.delete.staff-lest.error")
														});
											},
											argument:{
											}
										};
										YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
									} else {
										Alfresco.util.PopupManager.displayMessage(
												{
													text:me.msg("message.delete.staff-lest.error")
												});
									}
								},
								failure:function (oResponse) {
									Alfresco.util.PopupManager.displayMessage(
											{
												text:me.msg("message.delete.staff-lest.error")
											});
								}
							};
							YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
						}
					};

					new LogicECM.module.Base.DataGrid('${id}').setOptions(
							{
								usePagination:true,
								showExtendSearchBlock:${showSearchBlock?string},
                                showActionColumn: LogicECM.module.OrgStructure.IS_ENGINEER ? true : false,
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

				YAHOO.util.Event.onDOMReady(init);
			})
					();
			//]]></script>
		</@grid.datagrid>
		
	

