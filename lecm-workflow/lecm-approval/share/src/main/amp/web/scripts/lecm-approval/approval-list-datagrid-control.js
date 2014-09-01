if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};

(function() {

	LogicECM.module.Approval.ApprovalListDataGridControl = function(containerId, documentNodeRef) {
		var addItemDropdown = YAHOO.util.Dom.get(containerId + '-add-item-dropdown');

		this.documentNodeRef = documentNodeRef;
		if (addItemDropdown) {
			YAHOO.util.Event.on(addItemDropdown, 'change', this.onAddItemDropdownChange, this, true);
		}

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
		fireGridChanged: function() {
			YAHOO.Bubbling.fire('activeGridChanged', {
				datagridMeta: {
					itemType: this.stageType,
					nodeRef: this.currentIterationNode,
					useChildQuery: true,
					searchConfig: {
						filter: ''
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
		onAddItemDropdownChange: function(event) {
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
			console.log('_createApprovalListFromRoute');
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
						this.fireGridChanged();
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
						this.fireGridChanged();
					}
				},
				failureMessage: 'message.failure',
				execScripts: true,
				scope: this
			});
		}
	}, true);


})();
