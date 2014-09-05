if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};

(function() {

	LogicECM.module.Approval.ApprovalListDataGridControl = function(containerId, documentNodeRef) {
		var addStageButton = YAHOO.util.Dom.get(containerId + '-add-stage'),
			createApprovalListButton;

		this.documentNodeRef = documentNodeRef;

		createApprovalListButton = new YAHOO.widget.Button(containerId + '-create-approval-list-button', {
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

		if (addStageButton) {
			YAHOO.util.Event.on(addStageButton, 'click', this.onAddStageButton, this, true);
		}

		YAHOO.util.Event.delegate('Share', 'click', function() {
			LogicECM.module.Base.Util.printReport(this.documentNodeRef, this.options.reportId);
		}, '#printApprovalReport', this, true);

		YAHOO.util.Event.delegate('Share', 'click', function() {
			this.editIteration();
		}, '#editIteration', this, true);

		this.getApprovalData();
		YAHOO.Bubbling.on('activeTabChange', this.renewDatagrid, this);

		return LogicECM.module.Approval.ApprovalListDataGridControl.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Approval.ApprovalListDataGridControl, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Approval.ApprovalListDataGridControl.prototype, {
		stageType: null,
		stageItemType: null,
		routeType: null,
		currentIterationNode: null,
		approvalState: null,
		editItreationFormOpened: false,
		getApprovalData: function(callback, callbackArg) {
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

							LogicECM.module.Routes = LogicECM.module.Routes || {};
							LogicECM.module.Routes.Const = LogicECM.module.Routes.Const || {};
							LogicECM.module.Routes.Const.ROUTES_CONTAINER = LogicECM.module.Routes.Const.ROUTES_CONTAINER || {};
							LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageItemType = this.stageItemType;
							LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageType = this.stageType;
							LogicECM.module.Routes.Const.ROUTES_CONTAINER.routeType = this.routeType;

							if (YAHOO.lang.isFunction(callback)) {
								callback.call(this, callbackArg);
							}
						}
					}
				},
				failureMessage: 'message.failure',
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
				this.getApprovalData(this.fireGridChanged);
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

			if (this.approvalState === "ACTIVE") {
				Alfresco.util.PopupManager.displayPrompt({
					title: 'Итерация активна',
					text: 'Нельзя создать новый лист согласования, так как итерация активна'
				});
				return false;
			}

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
		_createApprovalListFromRoute: function() {
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
						this.currentIterationNode = r.json.nodeRef;
						this.approvalState = 'NEW';
						this.fireGridChanged(true);
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
						this.currentIterationNode = r.json.nodeRef;
						this.approvalState = 'NEW';
						this.fireGridChanged(true);
						if (YAHOO.lang.isFunction(callback)) {
							callback.apply(this, callbackArgsArr);
						}
					}
				},
				failureMessage: 'message.failure',
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
		}
	}, true);

})();
