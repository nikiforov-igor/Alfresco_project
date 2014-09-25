if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};

LogicECM.module.Approval.StageExpanded = LogicECM.module.Approval.StageExpanded || {};

(function () {

	LogicECM.module.Approval.ApprovalListDataGridControl = function (containerId, documentNodeRef) {
		var createApprovalListButtonElement, addStageButtonElement, clearButtonElement;
		this.documentNodeRef = documentNodeRef;

		createApprovalListButtonElement = YAHOO.util.Dom.get(containerId + '-create-approval-list-button');
		if (createApprovalListButtonElement) {
			this.createApprovalListButton = new YAHOO.widget.Button(createApprovalListButtonElement, {
				type: 'menu',
				menu: [{
						text: this.msg('label.button.create.approval.from.route'),
						value: 'route',
						disabled: false,
						onclick: {
							fn: this.onCreateApprovalListButtonClick,
							scope: this
						}
					}, {
						text: this.msg('label.button.create.approval.empty'),
						value: 'empty',
						disabled: false,
						onclick: {
							fn: this.onCreateApprovalListButtonClick,
							scope: this
						}
					}],
				disabled: false
			});
		}

		addStageButtonElement = YAHOO.util.Dom.get(containerId + '-add-stage');
		if (addStageButtonElement) {
			this.addStageButton = new YAHOO.widget.Button(addStageButtonElement);
			this.addStageButton.on('click', this.onAddStageButton, this, true);
		}

		clearButtonElement = YAHOO.util.Dom.get(containerId + '-clear-button');
		if (clearButtonElement) {
			this.clearButton = new YAHOO.widget.Button(clearButtonElement);
			this.clearButton.on('click', this.onClearButton, this, true);
		}

		this.approvalContainer = YAHOO.util.Dom.get(containerId + '-approval-container');
		this.completedApprovalsCountContainer = YAHOO.util.Dom.get(containerId + '-approval-completed-count');
		this.showHistoryLink = YAHOO.util.Dom.get(containerId + '-show-history-link');
		if (this.showHistoryLink) {
			YAHOO.util.Event.on(this.showHistoryLink, 'click', this.onShowHistoryButton, this, true);
		}

		this.sourceRouteInfoContainer = YAHOO.util.Dom.get(containerId + '-source-route-info');
		this.currentApprovalInfoContainer = YAHOO.util.Dom.get(containerId + '-current-approval-info');

		YAHOO.util.Event.delegate('Share', 'click', function () {
			LogicECM.module.Base.Util.printReport(this.documentNodeRef, this.options.reportId);
		}, '#printApprovalReport', this, true);

		YAHOO.util.Event.delegate('Share', 'click', function () {
			this.editIteration();
		}, '#editIteration', this, true);

		this.approvalStateSettings.NOT_EXITS.hideElements = [
			this.clearButton,
			this.approvalContainer
		];
		this.approvalStateSettings.NOT_EXITS.revealElements = [
			this.createApprovalListButton,
			this.addStageButton
		];

		this.approvalStateSettings.NEW.hideElements = [
			this.createApprovalListButton
		];
		this.approvalStateSettings.NEW.revealElements = [
			this.clearButton,
			this.approvalContainer,
			this.addStageButton
		];

		this.approvalStateSettings.ACTIVE.hideElements = [
			this.clearButton,
			this.createApprovalListButton
		];
		this.approvalStateSettings.ACTIVE.revealElements = [
			this.approvalContainer,
			this.addStageButton
		];

		this.approvalStateSettings.COMPLETE.hideElements = [
			this.clearButton,
			this.addStageButton
		];
		this.approvalStateSettings.COMPLETE.revealElements = [
			this.approvalContainer,
			this.createApprovalListButton
		];

		this.renewDatagrid();

		YAHOO.Bubbling.on('activeTabChange', this.renewDatagrid, this);
		YAHOO.Bubbling.on('stageItemDeleted', function () {
			this.getApprovalData(this.fillCurrentApprovalState);
		}, this);

		LogicECM.module.Approval.ApprovalListDataGridControl.superclass.constructor.call(this, containerId);

		this.name = 'LogicECM.module.Approval.ApprovalListDataGridControl';

		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Approval.ApprovalListDataGridControl, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Approval.ApprovalListDataGridControl.prototype, {
		stageType: null,
		stageItemType: null,
		routeType: null,
		currentIterationNode: null,
		approvalState: null,
		approvalResult: null,
		editItreationFormOpened: false,
		clearButton: null,
		approvalContainer: null,
		createApprovalListButton: null,
		addStageButton: null,
		completedApprovalsCountContainer: null,
		showHistoryLink: null,
		sourceRouteInfoContainer: null,
		currentApprovalInfoContainer: null,
		completedApprovalsCount: 0,
		sourceRouteInfo: null,
		approvalIsEditable: true,
		approvalHistoryFolder: null,
		approvalStateSettings: {
			NOT_EXITS: {
				stateMsg: 'Не существует',
				createButtonHandler: function (menuItemValue) {
					this._createApprovalList(menuItemValue);
				}
			},
			NEW: {
				stateMsg: 'Не начато'
			},
			ACTIVE: {
				stateMsg: 'Выполняется',
				createButtonHandler: function () {
					Alfresco.util.PopupManager.displayPrompt({
						title: this.msg('title.iteration.active'),
						text: this.msg('message.unable.to.create.list.active')
					});
				}
			},
			COMPLETE: {
				stateMsg: 'Завершено',
				createButtonHandler: function (menuItemValue) {
					var that = this;
					Alfresco.util.PopupManager.displayPrompt({
						title: this.msg('title.new.approval.list'),
						text: this.msg('message.new.approval.list.confirmation'),
						close: false,
						modal: true,
						buttons: [
							{
								text: this.msg('button.yes'),
								handler: function () {
									that._createApprovalList(menuItemValue);
									this.destroy();
								}
							}, {
								text: this.msg('button.no'),
								handler: function () {
									this.destroy();
								},
								isDefault: true
							}]
					});
				}

			}
		},
		getApprovalData: function (callback, callbackArgsArr) {
			Alfresco.util.Ajax.request({
				method: 'GET',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/routes/getRouteDataForDocument',
				dataObj: {
					documentNodeRef: this.documentNodeRef
				},
				successCallback: {
					scope: this,
					fn: function (response) {
						if (response) {
							this.stageType = response.json.stageType;
							this.stageItemType = response.json.stageItemType;
							this.routeType = response.json.routeType;
							this.currentIterationNode = response.json.currentIterationNode ? response.json.currentIterationNode : null;
							this.approvalState = response.json.approvalState;
							this.approvalResult = response.json.approvalResult;
							this.completedApprovalsCount = response.json.completedApprovalsCount;
							this.sourceRouteInfo = response.json.sourceRouteInfo;
							this.approvalIsEditable = response.json.approvalIsEditable;
							this.approvalHistoryFolder = response.json.approvalHistoryFolder;

							LogicECM.module.Routes = LogicECM.module.Routes || {};
							LogicECM.module.Routes.Const = LogicECM.module.Routes.Const || {};
							LogicECM.module.Routes.Const.ROUTES_CONTAINER = LogicECM.module.Routes.Const.ROUTES_CONTAINER || {};
							LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageItemType = this.stageItemType;
							LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageType = this.stageType;
							LogicECM.module.Routes.Const.ROUTES_CONTAINER.routeType = this.routeType;

							if (YAHOO.lang.isFunction(callback)) {
								callback.apply(this, callbackArgsArr);
							}
						}
					}
				},
				failureMessage: this.msg('message.failure'),
				execScripts: true,
				scope: this
			});
		},
		renewDatagrid: function (event, args) {
			function isDescendant(parent, child) {
				var node = child.parentNode;
				while (node !== null) {
					if (node === parent) {
						return true;
					}
					node = node.parentNode;
				}
				return false;
			}

			var currentTabDiv;
			if (event && event === 'activeTabChange' && args) {
				currentTabDiv = args[1].newValue.get('contentEl');
				if (!isDescendant(currentTabDiv, document.getElementById(this.id))) {
					return;
				}
			}

			if (!(this.stageType && this.stageItemType && this.routeType)) {
				this.getApprovalData(function () {
					this.fillCurrentApprovalState();
					this.manageControlsVisibility();
					this.fireGridChanged();
					YAHOO.util.Dom.removeClass(this.id, 'hidden');
				});
			} else {
				this.fireGridChanged();
			}

		},
		fireGridChanged: function (useChildQuery) {
			YAHOO.Bubbling.fire('activeGridChanged', {
				datagridMeta: {
					itemType: this.stageType,
					nodeRef: this.currentIterationNode,
					useChildQuery: !!useChildQuery,
					sort: 'cm:created|true',
					searchConfig: {
						filter: '-ASPECT:"sys:temporary" AND -ASPECT:"lecm-workflow:temp"'
					},
					actionsConfig: {
						fullDelete: true,
						trash: false
					}
				},
				bubblingLabel: this.id
			});
		},
		onCollapse: function (record) {
			var expandedRow = YAHOO.util.Dom.get(this.getExpandedRecordId(record));
			LogicECM.module.Base.Util.destroyForm(this.getExpandedFormId(record));
			expandedRow.parentNode.removeChild(expandedRow);
		},
		onCreateApprovalListButtonClick: function (event, eventArgs, menuItem) {
			var menuItemValue = menuItem.value;

			this.approvalStateSettings[this.approvalState].createButtonHandler.call(this, menuItemValue);

		},
		_createApprovalList: function (menuItemValue) {
			switch (menuItemValue) {
				case 'route' :
					this._createApprovalListFromRoute();
					break;
				case 'empty' :
					this._createEmptyApprovalLst(this.editIteration);
					break;
				default :
					break;
			}
		},
		_createApprovalListFromRoute: function (callback, callbackArgsArr) {
			var formId = 'selectRouteForm';
			var selectRouteForm = new Alfresco.module.SimpleDialog(this.id + '-' + formId);
			selectRouteForm.setOptions({
				width: '35em',
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
				actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/routes/convertRouteToIteration',
				templateRequestParams: {
					formId: formId,
					itemId: this.routeType,
					destination: this.documentNodeRef,
					itemKind: 'type',
					mode: 'create',
					showCancelButton: true,
					submitType: 'json'
				},
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function (form, simpleDialog) {
						simpleDialog.dialog.setHeader(this.msg('title.new.approval.list.from.route'));
						simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
							LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
							LogicECM.module.Base.Util.formDestructor(event, args, params);
						}, {moduleId: simpleDialog.id}, this);
					},
					scope: this
				},
				onSuccess: {
					fn: function (r) {
						var scriptErrors = r.json.scriptErrors,
							i, scriptErrorsLength = scriptErrors.length,
							macrosName, macrosScript, messageSplittedArr, message;

						for (i = 0; i < scriptErrorsLength; i++) {
							messageSplittedArr = scriptErrors[i].split(' | ');
							macrosName = messageSplittedArr.splice(0, 2)[1];
							macrosScript = messageSplittedArr.join(' | ');
							message = this.msg('message.error.running.macros') + ' ' + macrosName;
							this.displayErrorMessageWithDetails(this.msg('title.error.running.macros'), message, macrosScript);
						}
						this.getApprovalData(function () {
							this.fillCurrentApprovalState();
							this.manageControlsVisibility();
							this.fireGridChanged(true);
							if (YAHOO.lang.isFunction(callback)) {
								callback.apply(this, callbackArgsArr);
							}
							YAHOO.Bubbling.fire('redrawDocumentActions');
						});
					},
					scope: this
				},
				onFailure: {
					fn: function (response) {
						this.displayErrorMessageWithDetails(this.msg('logicecm.base.error'), this.msg('message.save.failure'), response.json.message);
					},
					scope: this
				}
			});

			selectRouteForm.show();
		},
		_createEmptyApprovalLst: function (callback, callbackArgsArr) {
			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/routes/createEmptyIteration',
				dataObj: {
					documentNodeRef: this.documentNodeRef
				},
				successCallback: {
					scope: this,
					fn: function (r) {
						this.getApprovalData(function () {
							this.fillCurrentApprovalState();
							this.manageControlsVisibility();
							this.fireGridChanged(true);
							if (YAHOO.lang.isFunction(callback)) {
								callback.apply(this, callbackArgsArr);
							}
							YAHOO.Bubbling.fire('redrawDocumentActions');
						});
					}
				},
				failureMessage: this.msg('message.failure'),
				execScripts: true,
				scope: this
			});
		},
		editIteration: function () {
			var errorText;

			if (this.editItreationFormOpened) {
				return;
			}

			switch (this.approvalState) {
				case 'ACTIVE':
					errorText = this.msg('message.unable.to.edit.running.iteration');
					break;
				case 'COMPLETE':
					errorText = this.msg('message.unable.to.edit.complete.iteration');
					break;
				case 'NOT_EXITS':
					this._createEmptyApprovalLst(this._showEditIterationDialog);
					break;
				default:
					this._showEditIterationDialog();
			}

			if (errorText) {
				Alfresco.util.PopupManager.displayPrompt({
					title: this.msg('title.unable.to.edit'),
					text: errorText
				});
				return;
			}

		},
		_showEditIterationDialog: function () {
			var formId = 'editIterationProperties',
				editIterationForm = new Alfresco.module.SimpleDialog(this.id + '-' + formId);

			this.editItreationFormOpened = true;

			editIterationForm.setOptions({
				width: '50em',
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
				templateRequestParams: {
					formId: formId,
					itemId: this.currentIterationNode,
					itemKind: 'node',
					mode: 'edit',
					showCancelButton: true,
					submitType: 'json'
				},
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function (form, simpleDialog) {
						simpleDialog.dialog.setHeader(this.msg('label.routes.edit-route.title'));
						this.editItreationFormOpened = false;
						simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
							LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
							LogicECM.module.Base.Util.formDestructor(event, args, params);
						}, {moduleId: simpleDialog.id}, this);
					},
					scope: this
				},
				failureMessage: 'Не удалось сохранить изменения итерации',
				successMessage: 'Изменения итерации сохранены'
			});

			editIterationForm.show();
		},
		onAddStageButton: function () {
			var createStageFunction = LogicECM.module.Routes.StagesControlDatagrid.prototype.onActionCreate;
			switch (this.approvalState) {
				case 'COMPLETE':
					Alfresco.util.PopupManager.displayPrompt({
						title: this.msg('title.unable.to.add.stage'),
						text: this.msg('message.unable.to.add.stage.iteration.completed')
					});
					break;
				case 'NOT_EXITS':
					this._createEmptyApprovalLst(createStageFunction, [null, null, true]);
					break;
				default:
					createStageFunction.call(this, null, null, true);
			}
		},
		onActionAddEmployee: function (item) {
			LogicECM.module.Routes.StagesControlDatagrid.prototype._createNewStageItem.call(this, 'employee', item.nodeRef);
		},
		onActionAddMacros: function (item) {
			LogicECM.module.Routes.StagesControlDatagrid.prototype._createNewStageItem.call(this, 'macros', item.nodeRef);
		},
		onActionEdit: function(item) {
			LogicECM.module.Routes.StagesControlDatagrid.prototype.onActionEdit.call(this, item);
		},
		onClearButton: function () {
			var that = this;
			Alfresco.util.PopupManager.displayPrompt({
				title: this.msg('title.approval.list.clear'),
				text: this.msg('message.clear.approval.list'),
				close: false,
				modal: true,
				buttons: [
					{
						text: this.msg('button.yes'),
						handler: function () {
							that._deleteApprovalList();
							this.destroy();
						}
					}, {
						text: this.msg('button.no'),
						handler: function () {
							this.destroy();
						},
						isDefault: true
					}]
			});
		},
		fillCurrentApprovalState: function () {
			var approvalMsgTemplate = '{msg} ({additionalMsg})';
			this.completedApprovalsCountContainer.innerHTML = this.completedApprovalsCount;
			this.sourceRouteInfoContainer.innerHTML = this.sourceRouteInfo ? YAHOO.lang.substitute(approvalMsgTemplate, {
				msg: this.msg('label.approval.typical'),
				additionalMsg: this.sourceRouteInfo
			}) : this.msg('label.approval.individual');
			this.currentApprovalInfoContainer.innerHTML = this.approvalState === 'COMPLETE' ? YAHOO.lang.substitute(approvalMsgTemplate, {
				msg: this.approvalStateSettings[this.approvalState].stateMsg,
				additionalMsg: this.approvalResult.title
			}) : this.approvalStateSettings[this.approvalState].stateMsg;
		},
		manageControlsVisibility: function () {
			function hide(element) {
				YAHOO.util.Dom.addClass(element, 'hidden');
			}

			function reveal(element) {
				YAHOO.util.Dom.removeClass(element, 'hidden');
			}

			if (this.completedApprovalsCount > 0) {
				reveal(this.showHistoryLink);
			} else {
				hide(this.showHistoryLink);
			}

			this.approvalStateSettings[this.approvalState].hideElements.forEach(hide);
			this.approvalStateSettings[this.approvalState].revealElements.forEach(reveal);

		},
		_deleteApprovalList: function (callback, callbackArgsArr) {
			var nodeRefObj;

			if (!this.currentIterationNode) {
				return;
			}

			nodeRefObj = new Alfresco.util.NodeRef(this.currentIterationNode);
			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'slingshot/doclib/action/aspects/node/' + nodeRefObj.uri,
				dataObj: {
					added: ['sys:temporary'],
					removed: []
				},
				successCallback: {
					fn: function (r) {
						Alfresco.util.Ajax.jsonRequest({
							method: 'DELETE',
							url: Alfresco.constants.PROXY_URI_RELATIVE + 'slingshot/doclib/action/folder/node/' + nodeRefObj.uri,
							successCallback: {
								fn: function (r) {
									this.getApprovalData(function () {
										this.fillCurrentApprovalState();
										this.manageControlsVisibility();
										if (YAHOO.lang.isFunction(callback)) {
											callback.apply(this, callbackArgsArr);
										}
										YAHOO.Bubbling.fire('redrawDocumentActions');
									});
								},
								scope: this
							},
							failureMessage: this.msg('message.failure')
						});
					},
					scope: this
				},
				failureMessage: this.msg('message.failure')
			});
		},
		onShowHistoryButton: function () {
			var formId = 'showApprovalHistoryForm';
			var showApprovalHistoryForm = new Alfresco.module.SimpleDialog(this.id + '-' + formId);
			showApprovalHistoryForm.setOptions({
				width: '80em',
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
				templateRequestParams: {
					formId: formId,
					itemId: this.approvalHistoryFolder,
					mode: 'edit',
					itemKind: 'node',
					showCancelButton: true,
					documentNodeRef: this.documentNodeRef
				},
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function (form, simpleDialog) {
						simpleDialog.dialog.setHeader(this.msg('title.approval.history'));
						simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
							LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
							LogicECM.module.Base.Util.formDestructor(event, args, params);
						}, {moduleId: simpleDialog.id}, this);
						form.setAJAXSubmit(false);
						simpleDialog.widgets.cancelButton.set('label', this.msg('button.close'));
						simpleDialog.widgets.okButton.addClass('hidden');
					},
					scope: this
				},
				onFailure: {
					fn: function (response) {
						this.displayErrorMessageWithDetails(this.msg('logicecm.base.error'), this.msg('message.save.failure'), response.json.message);
					},
					scope: this
				}
			});

			showApprovalHistoryForm.show();
		},
		onActionDelete: function (p_items, owner, actionsConfig, fnDeleteComplete) {
			this.onDelete(p_items, owner, actionsConfig, function () {
				this.getApprovalData(this.fillCurrentApprovalState);
			}, null);
		}
	}, true);

	LogicECM.module.Approval.StageExpanded.getCustomCellFormatter = function (grid, elCell, oRecord, oColumn, oData) {
		function formatState(nodeRef, decisionData, hasComment) {
			var result,
				commentIcon = '<img alt="Комментарий" src="' + Alfresco.constants.URL_RESCONTEXT + 'themes/lecmTheme/images/create-new-button.png">',
				messageTemplate = '<a href="javascript:void(0)" onclick="viewAttributes(\'{nodeRef}\', null, \'label.view.approval.details\', \'viewApprovalResult\')">{value} {icon}</a>';

			if (decisionData.value === 'NO_DECISION') {
				return null;
			}

			result = YAHOO.lang.substitute(messageTemplate, {
				nodeRef: nodeRef,
				value: decisionData.displayValue,
				icon: hasComment ? commentIcon : ''
			});

			return result;
		}
		var html = '', i, oDataLength, datalistColumn, data, decision, hasComment, nodeRef;

		if (oRecord && oColumn) {
			if (!oData) {
				oData = oRecord.getData('itemData')[oColumn.field];
			}

			if (oData) {
				datalistColumn = grid.datagridColumns[oColumn.key];
				if (datalistColumn) {
					oData = YAHOO.lang.isArray(oData) ? oData : [oData];
					for (i = 0, oDataLength = oData.length, data; i < oDataLength; i++) {
						data = oData[i];

						switch (datalistColumn.name) { //  меняем отрисовку для конкретных колонок
							case 'lecmApproveAspects:approvalState':
								decision = oRecord.getData('itemData')['prop_lecmApproveAspects_approvalDecision'];
								hasComment = !!(oRecord.getData('itemData')['prop_lecmApproveAspects_hasComment'] && oRecord.getData('itemData')['prop_lecmApproveAspects_hasComment'].value);
								nodeRef = oRecord.getData("nodeRef");
								html = formatState(nodeRef, decision, hasComment);
								break;
							default:
								break;
						}
					}
				}
			}
		}
		return html ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
	};

})();
