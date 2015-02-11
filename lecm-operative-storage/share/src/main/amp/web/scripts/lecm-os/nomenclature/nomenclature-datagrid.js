if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Nomenclature = LogicECM.module.Nomenclature || {};
LogicECM.module.Nomenclature.Datagrid = LogicECM.module.Nomenclature.Datagrid || {};

(function() {

    var Bubbling = YAHOO.Bubbling;

    LogicECM.module.Nomenclature.Datagrid = function(containerId) {

		this.actionsEnum = {
			onActionDeleteNDSection: "Удаление раздела номенклатуры дел",
			onActionCopyNDSection: "Копирование раздела номенклатуры дел",
			onActionMoveNDSection: "Перемещение раздела номенклатуры дел",
			onActionMoveNomenclatureCase: "Перемещение номенклатурного дела",
			onActionCopyNomenclatureCase : "Копирование номенклатурного дела",
			onActionMarkToDeleteNomenclatureCase: "Выделение номенклатурного дела к уничтожению",
			onActionDestroyNomenclatureCase: "Уничтожение номенклатурного дела",
			onActionOpenND: "Открытие номенклатурного дела",
			onActionCloseND: "Закрытие номенклатурного дела",
			onActionApproveNomenclatureYear: "Утверждение номенклатуры дел",
			onActionDeleteNomenclatureYear: "Удаление номенклатуры дел",
			onActionArchiveND: "Передача номенклатурного дела в архив",
			onActionCloseNomenclatureYear: "Закрытие номенклатуры дел",
			onActionDeleteND: "Удаление номенклатурного дела",
			onReCreateNomenclature: "Копирование номенклатуры"
		};

		LogicECM.module.Nomenclature.Datagrid.superclass.constructor.call(this, containerId);
		return this;

	};
	YAHOO.lang.extend(LogicECM.module.Nomenclature.Datagrid, LogicECM.module.Base.DataGrid);
	YAHOO.lang.augmentObject(LogicECM.module.Nomenclature.Datagrid.prototype, {

		getCustomCellFormatter: function ND_DataGrid_customRenderCellDataType(scope, elCell, oRecord, oColumn, oData) {
			var html = "", i, ii, columnContent, datalistColumn, data,
					clickHandledStringTemplate = '<a href="javascript:void(0);" onclick="{clickHandler}(\'{nodeRef}\')">{content}</a>';
			/**
			 * Alfresco Slingshot aliases
			 */
			var $html = Alfresco.util.encodeHTML,
				$links = Alfresco.util.activateLinks,
				$combine = Alfresco.util.combinePaths,
				$userProfile = Alfresco.util.userProfileLink;

			if (!oRecord) {
				oRecord = this.getRecord(elCell);
			}
			if (!oColumn) {
				oColumn = this.getColumn(elCell.parentNode.cellIndex);
			}

			if (oRecord && oColumn) {
				if (!oData) {
					oData = oRecord.getData("itemData")[oColumn.field];
				}

				if (oData) {
					var datalistColumn = scope.datagridColumns[oColumn.key];
					if (datalistColumn) {
						oData = YAHOO.lang.isArray(oData) ? oData : [oData];
						for (var i = 0, ii = oData.length, data; i < ii; i++) {
							data = oData[i];

							var columnContent = "";
							if(datalistColumn.name == "lecm-os:fake-type") {
								var type = oRecord.getData().type;
								if(type == "lecm-os:nomenclature-unit-section") {
									columnContent += Alfresco.util.message('lecm.os.lbl.section');
								} else {
									columnContent += Alfresco.util.message('lecm.os.lbl.document');
								}

							}

							switch (datalistColumn.name.toLowerCase()) { //  меняем отрисовку для конкретных колонок
								case "cm:title":
									columnContent += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + data.displayValue + "</a>";
									columnContent += '<br />';
									break;
								default:
									break;
							}

							if (scope.options.attributeForShow != null && datalistColumn.name == scope.options.attributeForShow) {
								html += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\', null, \'" + scope.options.viewFormTitleMsg + "\')\">" + columnContent + "</a>";
							} else if (scope.options.attributeForOpen != null && datalistColumn.name == scope.options.attributeForOpen) {
								html += "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + oRecord.getData("nodeRef") + "\'\">" + columnContent + "</a>";
							} else {
								html += columnContent;
							}

							if (i < ii - 1) {
								html += "<br />";
							}
						}
					}
				}
			}
			return html ? html : null;
		},

		_actionResponse: function(actionString){

			Bubbling.fire("datagridRefresh");
            Bubbling.fire("armRefreshSelectedTreeNode"); // обновить ветку в дереве

            Alfresco.util.PopupManager.displayMessage({
					text: Alfresco.util.message('lecm.os.msg.action') + ' "' + actionString + '" ' + Alfresco.util.message('lecm.os.msg.completed')
				});

		},

		createScriptFormFunction: function(p_items, actionId, callback){
			var me = this,
				item = {},
				items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];

			item.items = [];

			items.forEach(function (el){
				item.items.push(el.nodeRef);
			});

			var doBeforeDialogShow = function (p_form, p_dialog) {
				var contId = p_dialog.id + "-form-container";
				Alfresco.util.populateHTML(
					[contId + "_h", actionId ]
				);

				Dom.addClass(contId, "metadata-form-edit");
				this.doubleClickLock = false;

				p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
			};

			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "/lecm/components/form/script";
			var templateRequestParams = {
				itemKind: "type",
				itemId: actionId,
				formId: "scriptForm",
				mode: "create",
				submitType: "json",
				items: JSON.stringify(item.items)
			};

			// Using Forms Service, so always create new instance
			var scriptForm = new Alfresco.module.SimpleDialog(this.id + "-scriptForm");
			scriptForm.setOptions(
				{
					width: "40em",
					templateUrl: templateUrl,
					templateRequestParams: templateRequestParams,
					actionUrl: null,
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: doBeforeDialogShow,
						scope: this
					},
					onSuccess: {
						fn: function DataGrid_onActionCreate_success(response) {
							if (YAHOO.lang.isFunction(callback)) {
								callback.call(this);
							} else {
								this._actionResponse(actionId);
							}
						},
						scope: this
					},
					onFailure: {
						fn: function DataGrid_onActionCreate_failure(response) {
							Alfresco.util.PopupManager.displayMessage(
								{
									text: this.msg("message.save.failure")
								});
							this.doubleClickLock = false;
						},
						scope: this
					}
				}).show();

		},

		onActionDestroyNomenclatureCase: function(p_items, owner, actionsConfig, fnPrompt){
			this.ActionsClickAdapter(p_items, this.actionsEnum[owner.className], actionsConfig, fnPrompt);
		},

		onActionCopyNomenclatureCase: function(p_items, owner, actionsConfig, fnPrompt) {
			this.createScriptFormFunction(p_items, this.actionsEnum[owner.className]);
		},

		onActionMoveNomenclatureCase: function(p_items, owner, actionsConfig, fnPrompt) {
			this.createScriptFormFunction(p_items, this.actionsEnum[owner.className]);
		},

		onActionMarkToDeleteNomenclatureCase: function(p_items, owner, actionsConfig, fnPrompt) {
			this.ActionsClickAdapter(p_items, this.actionsEnum[owner.className], actionsConfig, fnPrompt);
		},

		onActionDeleteNDSection: function(p_items, owner, actionsConfig, fnPrompt) {
			this.ActionsClickAdapter(p_items, this.actionsEnum[owner.className], actionsConfig, fnPrompt);
		},

		onActionCopyNDSection: function(p_items, owner, actionsConfig, fnPrompt) {
			this.createScriptFormFunction(p_items, this.actionsEnum[owner.className]);
		},

		onActionMoveNDSection: function(p_items, owner, actionsConfig, fnPrompt) {
			this.createScriptFormFunction(p_items, this.actionsEnum[owner.className]);
		},

		onActionOpenND: function(p_items, owner, actionsConfig, fnPrompt) {
			this.ActionsClickAdapter(p_items, this.actionsEnum[owner.className], actionsConfig, fnPrompt);
		},

		onActionCloseND: function(p_items, owner, actionsConfig, fnPrompt) {
			this.ActionsClickAdapter(p_items, this.actionsEnum[owner.className], actionsConfig, fnPrompt);
		},

		onActionApproveNomenclatureYear: function(p_items, owner, actionsConfig, fnPrompt) {
			this.ActionsClickAdapter(p_items, this.actionsEnum[owner.className], actionsConfig, fnPrompt);
		},

		onActionDeleteNomenclatureYear: function(p_items, owner, actionsConfig, fnPrompt) {
			this.ActionsClickAdapter(p_items, this.actionsEnum[owner.className], actionsConfig, fnPrompt);
		},

		onActionArchiveND: function(p_items, owner, actionsConfig, fnPrompt) {
			this.ActionsClickAdapter(p_items, this.actionsEnum[owner.className], actionsConfig, fnPrompt);
		},

		onActionCloseNomenclatureYear: function(p_items, owner, actionsConfig, fnPrompt) {
			this.ActionsClickAdapter(p_items, this.actionsEnum[owner.className], actionsConfig, fnPrompt);
		},

		onActionDeleteND: function(p_items, owner, actionsConfig, fnPrompt) {
			this.ActionsClickAdapter(p_items, this.actionsEnum[owner.className], actionsConfig, fnPrompt);
		},

		onReCreateNomenclature: function(p_items, owner, actionsConfig, fnPrompt) {
			this.ActionsClickAdapter(p_items, this.actionsEnum[owner.className], actionsConfig, fnPrompt);
		},

		onActionEdit: function(item) {
			// Для предотвращения открытия нескольких карточек (при многократном быстром нажатии на кнопку редактирования)
			if (this.editDialogOpening) {
				return;
			}
			this.editDialogOpening = true;
			var me = this;

			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
			var templateRequestParams = {
				itemKind: "node",
				itemId: item.nodeRef,
				mode: "edit",
				submitType: "json",
				showCancelButton: true
			};


			// Using Forms Service, so always create new instance
			var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
			editDetails.setOptions(
				{
					width: this.options.editFormWidth,
					templateUrl:templateUrl,
					templateRequestParams:templateRequestParams,
					actionUrl:null,
					destroyOnHide:true,
					doBeforeDialogShow:{
						fn: function(p_form, p_dialog) {

							if(item.type == 'lecm-os:nomenclature-year-section') {
								var data = p_form.getFormData();
								var status = data['prop_lecm-os_nomenclature-year-section-status'];

								switch (status) {
									case 'PROJECT':
										p_dialog.widgets.okButton.set('label', "Утвердить");
										break;
									case 'APPROVED':
										p_dialog.widgets.okButton.set('label', "Закрыть");
										break;
								}
							}

							var contId = p_dialog.id + "-form-container";
							if (item.type && item.type != "") {
								Dom.addClass(contId, item.type.replace(":", "_") + "_edit");
							}
							p_dialog.dialog.setHeader(this.msg(this.options.editFormTitleMsg));
							this.editDialogOpening = false;

							p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
						},
						scope:this
					},
					doBeforeFormSubmit: {
						scope: this,
						fn: function() {



								var status = document.getElementsByName('prop_lecm-os_nomenclature-year-section-status')[0];
								var sortField = document.getElementsByName('prop_os-aspects_sort-value')[0];
								var unitIndexField = document.getElementsByName('prop_lecm-os_nomenclature-unit-section-index');
								var caseIndexField = document.getElementsByName('prop_lecm-os_nomenclature-case-index');

								if(status) {
									switch (status.value) {
										case 'PROJECT':
											this.ActionsClickAdapter(item, this.actionsEnum.onActionApproveNomenclatureYear);
											break;
										case 'APPROVED':
											this.ActionsClickAdapter(item, this.actionsEnum.onActionCloseNomenclatureYear, null, this.closeYearSection_Prompt_sync);
											break;
									}
								}

								if(unitIndexField && unitIndexField.length) {
									sortField.value = 'a' + unitIndexField[0].value;
									return;
								}

								if(caseIndexField && caseIndexField.length) {
									sortField.value = 'b' + caseIndexField[0].value;
									return;
								}

						}
					},
					onSuccess:{
						fn:function DataGrid_onActionEdit_success(response) {
							// Reload the node's metadata
							Bubbling.fire("datagridRefresh",
								{
									bubblingLabel:me.options.bubblingLabel
								});
                            Bubbling.fire("armRefreshSelectedTreeNode"); // обновить ветку в дереве
                            Alfresco.util.PopupManager.displayMessage({
								text:this.msg("message.details.success")
							});
							this.editDialogOpening = false;
						},
						scope:this
					},
					onFailure:{
						fn:function DataGrid_onActionEdit_failure(response) {
							Alfresco.util.PopupManager.displayMessage(
								{
									text:this.msg("message.details.failure")
								});
							this.editDialogOpening = false;
						},
						scope:this
					}
				}).show();
		},


		ActionsClickAdapter: function(item, actionId, actionsConfig, fnPrompt) {

			function execAction() {
				Alfresco.util.Ajax.jsonRequest({
					method: "POST",
					url: Alfresco.constants.PROXY_URI + "lecm/groupActions/exec",
					dataObj: {
						items: [item.nodeRef],
						actionId: actionId
					},
					successCallback: {
						fn: this._actionResponse.call(this, actionId)
					},
					failureCallback: {
						fn: function () {

						}
					},
					scope: this
				});
			};

			if(YAHOO.lang.isFunction(fnPrompt)) {
				fnPrompt.call(this, execAction, item);
			} else {
				execAction.call(this);
			}
		},

		deleteNDSection_Promt: function(execFunction, item) {

			Alfresco.util.Ajax.jsonRequest({
				method: "GET",
				url: Alfresco.constants.PROXY_URI + "lecm/dictionary/api/getChildrenItems.json?nodeRef=" + item.nodeRef,
				successCallback: {
					scope: this,
					fn: function (oResponse) {
						if(oResponse.json.length) {
							Alfresco.util.PopupManager.displayPrompt({
								title:Alfresco.util.message('lecm.os.lbl.remove.section'),
								text: Alfresco.util.message('lecm.os.msg.not.empty.sections')
							});
						} else {
							execFunction.call(this);
						}
					}
				},
				failureMessage: this.msg('message.failure'),
				scope: this
			});
		},

		deleteYearSection_Prompt: function(execFunction, item) {

			Alfresco.util.Ajax.jsonRequest({
				method: 'GET',
				url: Alfresco.constants.PROXY_URI + 'lecm/dictionary/api/getChildrenItems.json?nodeRef=' + item.nodeRef,
				successCallback: {
					scope: this,
					fn: function(response) {
						if(response.json.length) {
							Alfresco.util.PopupManager.displayPrompt({
								title:Alfresco.util.message('lecm.os.lbl.remove.nomen'),
								text: Alfresco.util.message('lecm.os.msg.not.empty.nomen')
							});
						} else {
							execFunction.call(this);
						}
					}
				},
				failureMessage: this.msg('message.failure'),
				scope: this
			});
		},

		closeYearSection_Prompt_sync: function(execFunction, item) {
			$.ajax({
				url: Alfresco.constants.PROXY_URI + 'lecm/os/nomenclature/getOpenTransientCases?nodeRef=' + item.nodeRef,
				context: this,
				success: function (response) {
						var items = response.items;
						if (items && items.length) {
							Alfresco.util.PopupManager.displayPrompt({
								title:'Закрытие номенклатуры дел',
								text: 'В текущей номенклатуре дел есть незакрытые переходящие дела. Выберите действие.',
								buttons:[{
									text: 'Закрыть все дела',
									handler: {
										obj: {
											context: this,
											fn: execFunction
										},
										fn: closeAllCases
									}
								}, {
									text: 'Перенести переходящие дела',
									handler: {
										obj: {
											context: this,
											fn: execFunction,
											items: items
										},
										fn: moveOpenTransientCases
									}
								}, {
									text: 'Отмена',
									handler: {
										fn: cancel
									}
								}]
							});
						} else {
							execFunction.call(this);
						}
					},
				async: false
			});
		},

		closeYearSection_Prompt: function(execFunction, item) {
			Alfresco.util.Ajax.jsonRequest({
				method: 'GET',
				url: Alfresco.constants.PROXY_URI + 'lecm/os/nomenclature/getOpenTransientCases?nodeRef=' + item.nodeRef,
				successCallback: {
					scope: this,
					fn: function(response) {
						var items = response.json.items;
						if (items && items.length) {
							Alfresco.util.PopupManager.displayPrompt({
								title:Alfresco.util.message('lecm.os.lbl.close.nomen'),
								text: Alfresco.util.message('lecm.os.msg.nomen.not.closed.docs'),
								buttons:[{
									text: Alfresco.util.message('lecm.os.msg.all.docs.close'),
									handler: {
										obj: {
											context: this,
											fn: execFunction
										},
										fn: closeAllCases
									}
								}, {
									text: Alfresco.util.message('lecm.os.msg.move.passing.docs'),
									handler: {
										obj: {
											context: this,
											fn: execFunction,
											items: items
										},
										fn: moveOpenTransientCases
									}
								}, {
									text: Alfresco.util.message('lecm.os.btn.cancel'),
									handler: {
										fn: cancel
									}
								}]
							});
						} else {
							execFunction.call(this);
						}
					}
				},
				failureMessage: this.msg('message.failure'),
				scope: this
			});

			function cancel(event, obj) {
				this.destroy();
			}

			function closeAllCases(event, obj) {
				this.destroy();
				obj.fn.call(obj.context);
			}

			function moveOpenTransientCases(event, obj) {
				this.destroy();
				obj.items.forEach(function(item, i, items) {
					items[i] = {
						nodeRef: item
					};
				});
				obj.context.createScriptFormFunction(obj.items, 'Перемещение номенклатурного дела', obj.fn);
			}
		},

		deleteND_Propmt: function(execFunction, item) {
			var nodeRef = new Alfresco.util.NodeRef(item.nodeRef);
			Alfresco.util.Ajax.jsonRequest({
				method: 'GET',
				url: Alfresco.constants.PROXY_URI + 'lecm/forms/picker/node/' + nodeRef.uri + '/children',
				successCallback: {
					scope: this,
					fn: function(response) {
						if(response.json.data.items.length) {
							Alfresco.util.PopupManager.displayPrompt({
								title:Alfresco.util.message('lecm.os.lbl.nomen.doc.remove'),
								text: Alfresco.util.message('lecm.os.msg.doc.contains.docs'),
								buttons:[
									{
										text:Alfresco.util.message('lecm.os.btn.ok'),
										handler: {
											obj: this,
											fn: destroyND
										}
									},
									{
										text:Alfresco.util.message('lecm.os.btn.cancel'),
										handler:function DataGridActions__onActionDelete_cancel() {
											this.destroy();
										}
									}
								]
							});
						} else {
							execFunction.call(this);
						}
					}
				},
				failureMessage: this.msg('message.failure'),
				scope: this
			});

			function destroyND(event, obj) {
				execFunction.call(obj);
				this.destroy();
			}
		},

		reCreateNomenclature_Prompt: function(execFunction, item) {
			var yearRef = item.itemData['prop_lecm-os_nomenclature-year-section-year'].value + 1;
			var orgRef = item.itemData['assoc_lecm-os_nomenclature-organization-assoc'].value;
			Alfresco.util.Ajax.jsonRequest({
				method: 'GET',
				url: Alfresco.constants.PROXY_URI + "lecm/os/nomenclature/isYearUniq?year=" + yearRef + "&orgNodeRef=" + orgRef,
				successCallback: {
					scope: this,
					fn: function(response) {
						if(!response.json.uniq) {
							Alfresco.util.PopupManager.displayMessage({
								text: 'Невозможно создать новую номенклатуру, так как она уже создана'
							});
						} else {
							execFunction.call(this);
						}
					}
				},
				failureMessage: this.msg('message.failure'),
				scope: this
			});
		},

		deleteNDSectionEvaluator: function(rowData) {
			var type = rowData.type,
				status = rowData.itemData["prop_lecm-os_nomenclature-unit-section-status"],
				statuses = ["PROJECT", "APPROVED", "CLOSED"];

			return ("lecm-os:nomenclature-unit-section" == type) && (statuses.indexOf(status.value) >= 0);
		},

		nomenclatureCaseEvaluator: function(rowData) {
			var type = rowData.type,
				status = rowData.itemData["prop_lecm-os_nomenclature-case-status"],
				statuses = ["PROJECT", "OPEN", "CLOSED"];

			return ("lecm-os:nomenclature-case" == type) && (statuses.indexOf(status.value) >= 0);
		},

		markToDeleteEvaluator: function(rowData) {
			var type = rowData.type,
				status = rowData.itemData["prop_lecm-os_nomenclature-case-status"];
			if(type != "lecm-os:nomenclature-case") return;

			return ("lecm-os:nomenclature-case" == type) && (status.value == "CLOSED");
		},

		destroyEvaluator: function(rowData) {
			var type = rowData.type,
				status = rowData.itemData["prop_lecm-os_nomenclature-case-status"];

			return ("lecm-os:nomenclature-case" == type) && (status.value == "MARK_TO_DESTROY");
		},

		openNDEvaluator: function(rowData) {
			var type = rowData.type,
				status = rowData.itemData["prop_lecm-os_nomenclature-case-status"],
				yearStatus = rowData.itemData["prop_lecm-os_nomenclature-year-section-status-fake"],
				statuses = ["CLOSED", "PROJECT"];

			return ("lecm-os:nomenclature-case" == type) && (statuses.indexOf(status.value) >= 0) && (yearStatus.value == "APPROVED");
		},

		closeNDEvaluator: function(rowData) {
			var type = rowData.type,
				status = rowData.itemData["prop_lecm-os_nomenclature-case-status"];

			return ("lecm-os:nomenclature-case" == type ) && (status.value == "OPEN");
		},

		approveNomenclatureYearEvaluator: function(rowData) {
			var type = rowData.type,
				status = rowData.itemData['prop_lecm-os_nomenclature-year-section-status'];

			return ('lecm-os:nomenclature-year-section' == type) && ('PROJECT' == status.value);
		},

		deleteNomenclatureYearEvaluator: function(rowData) {
			var type = rowData.type,
				statues = ['PROJECT', 'APPROVED', 'CLOSED'],
				status = rowData.itemData['prop_lecm-os_nomenclature-year-section-status'];

			return ('lecm-os:nomenclature-year-section' == type) && (statues.indexOf(status.value) >= 0);
		},

		archiveNDEvaluator: function(rowData) {
			var type = rowData.type,
				status = rowData.itemData["prop_lecm-os_nomenclature-case-status"],
				archive = rowData.itemData["prop_lecm-os_nomenclature-case-to-archive"];

			return (type == "lecm-os:nomenclature-case") && (status.value == "CLOSED") && (archive);
		},

		closeNomenclatureYearEvaluator: function(rowData) {
			var type = rowData.type,
				status = rowData.itemData['prop_lecm-os_nomenclature-year-section-status'];

			return ('lecm-os:nomenclature-year-section' == type) && ('APPROVED' == status.value);
		},

		deleteNDEvaluator: function(rowData) {
			var type = rowData.type,
				status = rowData.itemData["prop_lecm-os_nomenclature-case-status"],
				statuses = ['PROJECT', 'OPEN', 'CLOSED'];

			return (type == "lecm-os:nomenclature-case") && (statuses.indexOf(status.value) >= 0);
		},

		editArchivedEvaluator: function(rowData)  {
			var type = rowData.type;

			return (type == "lecm-os:nomenclature-case") && LogicECM.Nomenclature.isArchivist;
		},

		reCreateNomenclatureEvaluator: function(rowData) {
			var type = rowData.type,
				status = rowData.itemData['prop_lecm-os_nomenclature-year-section-status'];

			return ('lecm-os:nomenclature-year-section' == type) && ('APPROVED' == status.value);
		},

		editEvaluator: function(rowData) {
			return LogicECM.Nomenclature.isArchivist;
		}

	}, true);

})();