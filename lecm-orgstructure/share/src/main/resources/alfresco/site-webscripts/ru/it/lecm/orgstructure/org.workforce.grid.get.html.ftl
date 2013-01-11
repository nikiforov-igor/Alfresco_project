<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div  id="orgstructure-workforces-grid">
	<div id="yui-main-2">
		<div id="${id}-alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true>
			<script type="text/javascript">//<![CDATA[
			function createWorkforceDatagrid() {
				LogicECM.module.Base.DataGrid.prototype.onActionEmployeeAdd = function DataGridActions_onActionEmployeeAdd(p_item, owner, actionsConfig, fnCallback) {
					var me = this;
					var metaData = {
						itemType:"lecm-orgstr:employee-link",
						nodeRef:p_item.nodeRef
					};

					var onAddCallback = function (employeeRef) {
                        // Reload the node's metadata
                        YAHOO.Bubbling.fire("datagridRefresh",
                                {
                                    bubblingLabel:me.options.bubblingLabel
                                });
                        Alfresco.util.PopupManager.displayMessage(
                                {
                                    text:this.msg("message.employee.add.success")
                                });
					}.bind(me);

					this.createDialogShow(metaData, onAddCallback);

				};
				LogicECM.module.Base.DataGrid.prototype.onActionEmployeeDelete = function DataGridActions_onActionEmployeeDelete(p_item, owner, actionsConfig, fnDeleteComplete) {
					var me = this;
					var staffRow = p_item;
					// Получаем для трудового ресурса (участника раб. группы) ссылку на сотрудника
					var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getStaffEmployeeLink?nodeRef=" + staffRow.nodeRef;
					var callback = {
						success:function (oResponse) {
							var oResult = eval("(" + oResponse.responseText + ")");
							if (oResult) {
								var onPrompt = function (fnAfterPrompt) {
									Alfresco.util.PopupManager.displayPrompt(
											{
												title:this.msg("message.employee.role.delete.title"),
												text: this.msg("message.employee.role.delete.prompt",
														staffRow.itemData["assoc_lecm-orgstr_element-member-employee-assoc"].displayValue,
														staffRow.itemData["assoc_lecm-orgstr_element-member-position-assoc"].displayValue),
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
								var fnDeleteComplete = function () {
									// Reload the node's metadata
									Alfresco.util.Ajax.jsonPost(
											{
												url:Alfresco.constants.PROXY_URI + "lecm/base/item/node/" + new Alfresco.util.NodeRef(p_item.nodeRef).uri,
												dataObj:this._buildDataGridParams(),
												successCallback:{
													fn:function DataGrid_onActionEdit_refreshSuccess(response) {
														// Fire "itemUpdated" event
														YAHOO.Bubbling.fire("dataItemUpdated",
																{
																	item:response.json.item,
																	bubblingLabel:me.options.bubblingLabel
																});
													},
													scope:this
												},
												failureCallback:{
													fn:function DataGrid_onActionEdit_refreshFailure(response) {
														Alfresco.util.PopupManager.displayMessage(
																{
																	text:this.msg("message.details.failure")
																});
													},
													scope:this
												}
											});
								}.bind(me);

								me.onDelete([oResult], owner, {fullDelete:true, targetDelete:true, successMessage: "message.employee.role.delete.success"}, fnDeleteComplete, onPrompt);
							} else {
								Alfresco.util.PopupManager.displayMessage(
										{
											text:this.msg("message.employee.role.delete.failure")
										});
							}
						},
						failure:function (oResponse) {
							Alfresco.util.PopupManager.displayMessage(
									{
										text:this.msg("message.employee.role.delete.failure")
									});
						}
					};
					YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
				};

				var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							bubblingLabel: "${bubblingLabel!"workForce"}",
							usePagination:true,
							showExtendSearchBlock:false,
							actions: [
								{
									type:"action-link-${bubblingLabel!"workForce"}",
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
									type:"action-link-${bubblingLabel!"workForce"}",
									id:"onActionEmployeeDelete",
									permission:"edit",
									label:"${msg("actions.deleteEmployee")}",
									evaluator: function (rowData) {
                                        var itemData = rowData.itemData;
                                        return itemData["assoc_lecm-orgstr_element-member-employee-assoc"] != "undefined" &&
		                                        itemData["assoc_lecm-orgstr_element-member-employee-assoc"].value.length > 0;
                                    }
								},
								{
									type:"action-link-${bubblingLabel!"workForce"}",
									id:"onActionEdit",
									permission:"edit",
									label:"${msg("actions.edit")}"
								},
								{
									type:"action-link-${bubblingLabel!"workForce"}",
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
							attributeForShow:"lecm-orgstr:element-member-position-assoc"
						}).setMessages(${messages});
                datagrid.draw();
			}

			function init() {
                createWorkforceDatagrid();
			}

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
