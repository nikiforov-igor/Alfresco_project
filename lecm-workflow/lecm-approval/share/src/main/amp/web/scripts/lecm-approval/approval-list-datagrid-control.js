if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};

(function() {

	LogicECM.module.Approval.ApprovalListDataGridControl = function(containerId, documentNodeRef) {
		var createApprovalListDropdown = YAHOO.util.Dom.get(containerId + '-create-approval-list');
		var addStageButton = YAHOO.util.Dom.get(containerId + '-add-stage');

		this.documentNodeRef = documentNodeRef;

		if (createApprovalListDropdown) {
			YAHOO.util.Event.on(createApprovalListDropdown, 'change', this.onCreateApprovalListDropdownChange, this, true);
		}

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
		onCreateApprovalListDropdownChange: function(event) {
			var dropdownMenu = event.target,
				dropdownMenuValue = dropdownMenu.value;

			dropdownMenu.selectedIndex = 0;

			if (this.approvalState === "ACTIVE") {
				Alfresco.util.PopupManager.displayPrompt({
					title: 'Итерация активна',
					text: 'Нельзя создать новый лист согласования, так как итерация активна'
				});
				return false;
			}

			switch (dropdownMenuValue) {
				case 'route' :
					this._createApprovalListFromRoute();
					break;
				case 'empty' :
					this._createEmptyApprovalLst();
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
		_createEmptyApprovalLst: function() {
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
					}
				},
				failureMessage: 'message.failure',
				execScripts: true,
				scope: this
			});
		},
		editIteration: function() {
			var errorText,
				formId = 'editIterationProperties';

			if (this.editItreationFormOpened) {
				return;
			}


			switch (this.approvalState) {
				case 'ACTIVE':
					errorText = 'Редактирование параметров запущенной итерации невозможно';
					break;
				case 'COMPLETE':
					errorText = 'Редактирвоание параметров завершенной итерации невозможно';
					break;
				case 'NOT_EXITS':
					errorText = 'Список согласования отсутствует';
					break;
			}

			if (errorText) {
				Alfresco.util.PopupManager.displayPrompt({
					title: 'Редактирование невозможно',
					text: errorText
				});
				return;
			}

			this.editItreationFormOpened = true;

			var editIterationForm = new Alfresco.module.SimpleDialog(this.id + '-' + formId);

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
			LogicECM.module.Routes.StagesControlDatagrid.prototype.onActionCreate.call(this);
		}
	}, true);


})();
