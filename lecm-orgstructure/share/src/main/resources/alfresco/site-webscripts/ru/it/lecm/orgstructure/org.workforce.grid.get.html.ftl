<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div  id="orgstructure-workforces-grid">
	<div id="yui-main-2">
		<div id="${id}-alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=false>
			<script type="text/javascript">//<![CDATA[
			function createWorkforceDatagrid() {
				LogicECM.module.Base.DataGrid.prototype.onActionEmployeeAdd = function DataGridActions_onActionEmployeeAdd(p_item, owner, actionsConfig, fnCallback) {
					var me = this;
					var metaData = {
						itemType:"lecm-orgstr:employee-link",
						nodeRef:p_item.nodeRef,
						createFormId:"el-work"
					};

					var onAddCallback = function (employeeRef) {
						// создаем ассоциацию
						var onSuccess = function DataGrid_onActionEmployeeAdd_onSuccess(response) {
							var createdAssoc = response.json.createdAssoc;
							if (createdAssoc) {
								// Reload the node's metadata
                                YAHOO.Bubbling.fire("datagridRefresh",
                                        {
                                            bubblingLabel:me.options.bubblingLabel
                                        });
								Alfresco.util.PopupManager.displayMessage(
										{
											text:this.msg("message.employee.add.success")
										});
							} else {
								onFailure.call(me, response);
							}
						};
						var onFailure = function DataGrid_onActionEmployeeAdd_onFailure(response) {
							// при создание ассоциации произошла ошибка - удаляем ссылку на сотрудника
							this.onDelete([{nodeRef:employeeRef}], owner, {fullDelete:true, targetDelete:true}, fnCallback, null);
							Alfresco.util.PopupManager.displayMessage(
									{
										text:this.msg("message.employee.add.failure")
									});
						};
						Alfresco.util.Ajax.jsonRequest(
								{
									url:Alfresco.constants.PROXY_URI + "lecm/base/createAssoc",
									method:"POST",
									dataObj:{
										source:p_item.nodeRef,
										target:employeeRef,
										assocType:"lecm-orgstr:element-member-employee-assoc"
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

				LogicECM.module.Base.DataGrid.prototype.deleteWorkForceEvaluator = function DataGridActions_deleteStaffEvaluator(rowData) {
					var itemData = rowData.itemData;
					return itemData["assoc_lecm-orgstr_element-member-employee-assoc"] == undefined || itemData["assoc_lecm-orgstr_element-member-employee-assoc"].value.length == 0;
				};
				LogicECM.module.Base.DataGrid.prototype.addEmployeeEvaluator = function DataGridActions_addEmployeeEvaluator(rowData) {
					var itemData = rowData.itemData;
					return itemData["assoc_lecm-orgstr_element-member-employee-assoc"] == undefined || itemData["assoc_lecm-orgstr_element-member-employee-assoc"].value.length == 0;
				};
				LogicECM.module.Base.DataGrid.prototype.deleteEmployeeEvaluator = function DataGridActions_addEmployeeEvaluator(rowData) {
					var itemData = rowData.itemData;
					return itemData["assoc_lecm-orgstr_element-member-employee-assoc"] != undefined && itemData["assoc_lecm-orgstr_element-member-employee-assoc"].value.length > 0;
				};

				new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							bubblingLabel: "${bubblingLabel!"workForce"}",
							usePagination:true,
							showExtendSearchBlock:false,
							actions: [
								{
									type:"action-link-workForce",
									id:"onActionEmployeeAdd",
									permission:"edit",
									label:"${msg("actions.addEmployee")}",
									evaluator:"addEmployeeEvaluator"
								},
								{
									type:"action-link-workForce",
									id:"onActionEmployeeDelete",
									permission:"edit",
									label:"${msg("actions.deleteEmployee")}",
									evaluator:"deleteEmployeeEvaluator"
								},
								{
									type:"action-link-workForce",
									id:"onActionEdit",
									permission:"edit",
									label:"${msg("actions.edit")}"
								},
								{
									type:"action-link-workForce",
									id:"onActionDelete",
									permission:"delete",
									label:"${msg("actions.delete-row")}",
									evaluator: "deleteWorkForceEvaluator"
								}
							],
							showCheckboxColumn: false
						}).setMessages(${messages});
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
