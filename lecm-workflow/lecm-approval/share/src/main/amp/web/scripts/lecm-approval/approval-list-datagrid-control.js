if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};

(function() {

	LogicECM.module.Approval.ApprovalListDataGridControl = function(containerId, documentNodeRef) {
		this.documentNodeRef = documentNodeRef;

		this.createApprovalListButton = new YAHOO.widget.Button(containerId + '-create-approval-list-button', {
			type: 'menu',
			menu: [{
					text: 'Список из маршрута',
					value: 'route',
					disabled: false,
					onclick: {
						fn: this.onCreateApprovalListButtonClick,
						scope: this
					}
				}, {
					text: 'Пустой список',
					value: 'empty',
					disabled: false,
					onclick: {
						fn: this.onCreateApprovalListButtonClick,
						scope: this
					}
				}],
			disabled: false
		});

		this.addStageButton = new YAHOO.widget.Button(containerId + '-add-stage');

		if (this.addStageButton) {
			this.addStageButton.on('click', this.onAddStageButton, this, true);
		}

		this.clearButton = new YAHOO.widget.Button(containerId + '-clear-button');
		if (this.clearButton) {
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

		YAHOO.util.Event.delegate('Share', 'click', function() {
			LogicECM.module.Base.Util.printReport(this.documentNodeRef, this.options.reportId);
		}, '#printApprovalReport', this, true);

		YAHOO.util.Event.delegate('Share', 'click', function() {
			this.editIteration();
		}, '#editIteration', this, true);

		this.getApprovalData(function() {
			this.fillCurrentApprovalState();
			this.manageControlsVisibility();
		});

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

		YAHOO.Bubbling.on('activeTabChange', this.renewDatagrid, this);

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
		approvalStateSettings: {
			NOT_EXITS: {
				stateMsg: 'Не существует',
				createButtonHandler: function(menuItemValue) {
					this._createApprovalList(menuItemValue);
				}
			},
			NEW: {
				stateMsg: 'Не начато'
			},
			ACTIVE: {
				stateMsg: 'Выполнятеся',
				createButtonHandler: function() {
					Alfresco.util.PopupManager.displayPrompt({
						title: 'Итерация активна',
						text: 'Нельзя создать новый лист согласования, так как итерация активна'
					});
				}
			},
			COMPLETE: {
				stateMsg: 'Завершено',
				createButtonHandler: function(menuItemValue) {
					var that = this;
					Alfresco.util.PopupManager.displayPrompt({
						title: 'Создание нового листа согласования',
						text: 'Вы действительно хотите создать новый лист согласования?',
						close: false,
						modal: true,
						buttons: [
							{
								text: this.msg("button.yes"),
								handler: function() {
									that._createApprovalList(menuItemValue);
									this.destroy();
								}
							}, {
								text: this.msg("button.no"),
								handler: function() {
									this.destroy();
								},
								isDefault: true
							}]
					});
				}

			}
		},
		getApprovalData: function(callback, callbackArgsArr) {
			Alfresco.util.Ajax.request({
				method: 'GET',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/routes/getRouteDataForDocument',
				dataObj: {
					documentNodeRef: this.documentNodeRef
				},
				successCallback: {
					scope: this,
					fn: function(response) {
						if (response) {
							this.stageType = response.json.stageType;
							this.stageItemType = response.json.stageItemType;
							this.routeType = response.json.routeType;
							this.currentIterationNode = response.json.currentIterationNode ? response.json.currentIterationNode : null;
							this.approvalState = response.json.approvalState;
							this.completedApprovalsCount = response.json.completedApprovalsCount;
							this.sourceRouteInfo = response.json.sourceRouteInfo;
							this.approvalIsEditable = response.json.approvalIsEditable;

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
		renewDatagrid: function(event, args) {
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
				this.getApprovalData(function() {
					this.fillCurrentApprovalState();
					this.manageControlsVisibility();
					this.fireGridChanged();
				});
			} else {
				this.fireGridChanged();
			}

		},
		fireGridChanged: function(useChildQuery) {
			YAHOO.Bubbling.fire('activeGridChanged', {
				datagridMeta: {
					itemType: this.stageType,
					nodeRef: this.currentIterationNode,
					useChildQuery: !!useChildQuery,
					sort: 'cm:title|true',
					searchConfig: {
						filter: '-ASPECT:"sys:temporary" AND -ASPECT:"lecm-workflow:temp"'
					}
				},
				bubblingLabel: this.id
			});
		},
		onCollapse: function(record) {
			var expandedRow = YAHOO.util.Dom.get(this.getExpandedRecordId(record));
			LogicECM.module.Base.Util.destroyForm(this.getExpandedFormId(record));
			expandedRow.parentNode.removeChild(expandedRow);
		},
		onCreateApprovalListButtonClick: function(event, eventArgs, menuItem) {
			var menuItemValue = menuItem.value;

			this.approvalStateSettings[this.approvalState].createButtonHandler.call(this, menuItemValue);

		},
		_createApprovalList: function(menuItemValue) {
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
		_createApprovalListFromRoute: function(callback, callbackArgsArr) {
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
					fn: function(form, simpleDialog) {
						simpleDialog.dialog.setHeader('Создать лист согласования из маршрута');
						simpleDialog.dialog.subscribe('destroy', function(event, args, params) {
							LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
							LogicECM.module.Base.Util.formDestructor(event, args, params);
						}, {moduleId: simpleDialog.id}, this);
					},
					scope: this
				},
				onSuccess: {
					fn: function(r) {
						this.getApprovalData(function() {
							this.fillCurrentApprovalState();
							this.manageControlsVisibility();
							this.fireGridChanged(true);
							if (YAHOO.lang.isFunction(callback)) {
								callback.apply(this, callbackArgsArr);
							}
						});
					},
					scope: this
				},
				onFailure: {
					fn: function(response) {
						this.displayErrorMessageWithDetails(this.msg('logicecm.base.error'), this.msg('message.save.failure'), response.json.message);
					},
					scope: this
				}
			});

			selectRouteForm.show();
		},
		_createEmptyApprovalLst: function(callback, callbackArgsArr) {
			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/routes/createEmptyIteration',
				dataObj: {
					documentNodeRef: this.documentNodeRef
				},
				successCallback: {
					scope: this,
					fn: function(r) {
						this.getApprovalData(function() {
							this.fillCurrentApprovalState();
							this.manageControlsVisibility();
							this.fireGridChanged(true);
							if (YAHOO.lang.isFunction(callback)) {
								callback.apply(this, callbackArgsArr);
							}
						});
					}
				},
				failureMessage: this.msg('message.failure'),
				execScripts: true,
				scope: this
			});
		},
		editIteration: function() {
			var errorText;

			if (this.editItreationFormOpened) {
				return;
			}

			switch (this.approvalState) {
				case 'ACTIVE':
					errorText = 'Редактирование параметров запущенной итерации невозможно';
					break;
				case 'COMPLETE':
					errorText = 'Редактирование параметров завершенной итерации невозможно';
					break;
				case 'NOT_EXITS':
					this._createEmptyApprovalLst(this._showEditIterationDialog);
					break;
				default:
					this._showEditIterationDialog();
			}

			if (errorText) {
				Alfresco.util.PopupManager.displayPrompt({
					title: 'Редактирование невозможно',
					text: errorText
				});
				return;
			}

		},
		_showEditIterationDialog: function() {
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
					fn: function(form, simpleDialog) {
						simpleDialog.dialog.setHeader(this.msg('label.routes.edit-route.title'));
						this.editItreationFormOpened = false;
						simpleDialog.dialog.subscribe('destroy', function(event, args, params) {
							LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
							LogicECM.module.Base.Util.formDestructor(event, args, params);
						}, {moduleId: simpleDialog.id}, this);
					},
					scope: this
				},
				onSuccess: {
					fn: function(r) {

					},
					scope: this
				},
				onFailure: {
					fn: function(r) {
						Alfresco.util.PopupManager.displayMessage({
							text: 'Не удалось отредактировать паратеры итерации: ' + r.json.message
						});
					},
					scope: this
				}
			});

			editIterationForm.show();
		},
		onAddStageButton: function() {
			var createStageFunction = LogicECM.module.Routes.StagesControlDatagrid.prototype.onActionCreate;
			switch (this.approvalState) {
				case 'COMPLETE':
					Alfresco.util.PopupManager.displayPrompt({
						title: 'Добавление этапа невозможно',
						text: 'Невозможно добавить этап в завершенную итерацию'
					});
					break;
				case 'NOT_EXITS':
					this._createEmptyApprovalLst(createStageFunction, [null, null, true]);
					break;
				default:
					createStageFunction.call(this, null, null, true);
			}
		},
		onActionAddEmployee: function(item) {
			LogicECM.module.Routes.StagesControlDatagrid.prototype._createNewStageItem.call(this, 'employee', item.nodeRef);
		},
		onActionAddMacros: function(item) {
			LogicECM.module.Routes.StagesControlDatagrid.prototype._createNewStageItem.call(this, 'macros', item.nodeRef);
		},
		onClearButton: function() {
			var that = this;
			Alfresco.util.PopupManager.displayPrompt({
				title: 'Очистка листа согласования',
				text: 'Вы действительно хотите очистить лист согласования?',
				close: false,
				modal: true,
				buttons: [
					{
						text: this.msg("button.yes"),
						handler: function() {
							that._deleteApprovalList();
							this.destroy();
						}
					}, {
						text: this.msg("button.no"),
						handler: function() {
							this.destroy();
						},
						isDefault: true
					}]
			});
		},
		fillCurrentApprovalState: function() {
			this.completedApprovalsCountContainer.innerHTML = this.completedApprovalsCount;
			this.sourceRouteInfoContainer.innerHTML = this.sourceRouteInfo;
			this.currentApprovalInfoContainer.innerHTML = this.approvalStateSettings[this.approvalState].stateMsg;
		},
		manageControlsVisibility: function() {
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
		_deleteApprovalList: function(callback, callbackArgsArr) {
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
					fn: function(r) {
						Alfresco.util.Ajax.jsonRequest({
							method: 'DELETE',
							url: Alfresco.constants.PROXY_URI_RELATIVE + 'slingshot/doclib/action/folder/node/' + nodeRefObj.uri,
							successCallback: {
								fn: function(r) {
									this.getApprovalData(function() {
										this.fillCurrentApprovalState();
										this.manageControlsVisibility();
										if (YAHOO.lang.isFunction(callback)) {
											callback.apply(this, callbackArgsArr);
										}
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
		onShowHistoryButton: function() {
			// TODO Добавить диалог отображения истории
			console.log('onShowHistoryButton');
		}
	}, true);
})();
