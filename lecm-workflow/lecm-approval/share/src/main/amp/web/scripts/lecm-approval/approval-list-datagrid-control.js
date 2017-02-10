/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};

LogicECM.module.Approval.StageExpanded = LogicECM.module.Approval.StageExpanded || {};

(function () {

	LogicECM.module.Approval.ApprovalListDataGridControl = function (containerId, documentNodeRef) {
		var createApprovalListButtonElement, addStageButtonElement, clearButtonElement, expandAllStagesElement;
		this.documentNodeRef = documentNodeRef;

		createApprovalListButtonElement = YAHOO.util.Dom.get(containerId + '-create-approval-list-button');
		if (createApprovalListButtonElement) {
			var menus =  [{
				url: 'javascript:void(0)',
				text: Alfresco.util.message('label.button.create.approval.from.route'),
				value: 'route',
				disabled: false,
				onclick: {
					fn: this.onCreateApprovalListButtonClick,
					scope: this
				}
			}, {
				url: 'javascript:void(0)',
				text: Alfresco.util.message('label.button.create.approval.empty'),
				value: 'empty',
				disabled: false,
				onclick: {
					fn: this.onCreateApprovalListButtonClick,
					scope: this
				}
			}, {
				url: 'javascript:void(0)',
				text: 'Предыдущее согласование',
				value: 'previous',
				disabled: false,
				classname: 'hidden',
				onclick: {
					fn: this.onCreateApprovalListButtonClick,
					scope: this
				}
			}];

			var casesMenu = new YAHOO.widget.Menu(containerId + "-approvalListMenu", {zIndex:1000, itemData:[{
				url: 'javascript:void(0)',
				text: Alfresco.util.message('label.button.create.approval.from.route'),
				value: 'route',
				disabled: false,
				onclick: {
					fn: this.onCreateApprovalListButtonClick,
					scope: this
				}
			}, {
				url: 'javascript:void(0)',
				text: Alfresco.util.message('label.button.create.approval.empty'),
				value: 'empty',
				disabled: false,
				onclick: {
					fn: this.onCreateApprovalListButtonClick,
					scope: this
				}
			}, {
				url: 'javascript:void(0)',
				text: 'Предыдущее согласование',
				value: 'previous',
				disabled: false,
				classname: 'hidden',
				onclick: {
					fn: this.onCreateApprovalListButtonClick,
					scope: this
				}
			}]});
			casesMenu.render(YAHOO.util.Dom.get(containerId));
			casesMenu.itemData = menus;

			this.createApprovalListButton = new YAHOO.widget.Button(createApprovalListButtonElement, {
				type: 'menu',
				menu: casesMenu,
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

		expandAllStagesElement = YAHOO.util.Dom.get(containerId + '-expand-all-button');
		if (expandAllStagesElement) {
			this.expandAllStagesButton = new YAHOO.widget.Button(expandAllStagesElement);
            this.expandAllStagesButton.on('click', this.onExpandAllStages, this, true);
		}

		this.approvalContainer = YAHOO.util.Dom.get(containerId + '-approval-container');
		this.completedApprovalsCountContainer = YAHOO.util.Dom.get(containerId + '-approval-completed-count');
		this.showHistoryLink = YAHOO.util.Dom.get(containerId + '-show-history-link');
		if (this.showHistoryLink) {
			YAHOO.util.Event.on(this.showHistoryLink, 'click', this.onShowHistoryButton, this, true);
		}

		this.sourceRouteInfoContainer = YAHOO.util.Dom.get(containerId + '-source-route-info');
		this.currentApprovalInfoContainer = YAHOO.util.Dom.get(containerId + '-current-approval-info');

		this.printReportButton = YAHOO.util.Dom.get(containerId + '-printApprovalReport');
		if (this.printReportButton) {
			YAHOO.util.Event.on(this.printReportButton, 'click', function () {
				LogicECM.module.Base.Util.printReport(this.documentNodeRef, this.options.reportId);
			}, this, true);
		}

		this.editIterationButton = YAHOO.util.Dom.get(containerId + '-editIteration');
		if (this.editIterationButton) {
			YAHOO.util.Event.on(this.editIterationButton, 'click', this.editIteration, this, true);
		}

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
            this.expandAllStagesButton,
			this.approvalContainer,
			this.addStageButton
		];

		this.approvalStateSettings.ACTIVE.hideElements = [
			this.clearButton,
			this.createApprovalListButton
		];
		this.approvalStateSettings.ACTIVE.revealElements = [
			this.approvalContainer,
			this.addStageButton,
            this.expandAllStagesButton
		];

		this.approvalStateSettings.COMPLETE.hideElements = [
			this.clearButton,
			this.addStageButton
		];
		this.approvalStateSettings.COMPLETE.revealElements = [
			this.approvalContainer,
			this.createApprovalListButton,
            this.expandAllStagesButton
		];

		YAHOO.Bubbling.on("hideControl", this.onHideControl, this);
		YAHOO.Bubbling.on("showControl", this.onShowControl, this);


		YAHOO.Bubbling.on('activeTabChange', this.renewDatagrid, this);
		YAHOO.Bubbling.on('stageItemDeleted', function () {
			this.getApprovalData(this.fillCurrentApprovalState);
		}, this);

		LogicECM.module.Approval.ApprovalListDataGridControl.superclass.constructor.call(this, containerId);

		this.name = 'LogicECM.module.Approval.ApprovalListDataGridControl';

		YAHOO.Bubbling.on("dataItemsDeleted", this.onDataItemsDeleted, this);

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
		completedCurrentApprovalsCount: 0,
		completedHistoryApprovalsCount: 0,
		sourceRouteInfo: null,
		approvalIsEditable: true,
		approvalHistoryFolder: null,
		approvalStateSettings: {
			NOT_EXITS: {
				stateMsg: Alfresco.util.message('message.not.exists'),
				createButtonHandler: function (menuItemValue) {
					this._createApprovalList(menuItemValue);
				}
			},
			NEW: {
				stateMsg: Alfresco.util.message('message.not.started')
			},
			ACTIVE: {
				stateMsg: Alfresco.util.message('message.performed'),
				createButtonHandler: function () {
					Alfresco.util.PopupManager.displayPrompt({
						title: Alfresco.util.message('title.iteration.active'),
						text: Alfresco.util.message('message.unable.to.create.list.active')
					});
				}
			},
			COMPLETE: {
				stateMsg: Alfresco.util.message('message.completed'),
				createButtonHandler: function (menuItemValue) {
					var that = this;
					Alfresco.util.PopupManager.displayPrompt({
						title: Alfresco.util.message('title.new.approval.list'),
						text: Alfresco.util.message('message.new.approval.list.confirmation'),
						close: false,
						modal: true,
						buttons: [
							{
								text: Alfresco.util.message('button.yes'),
								handler: function () {
									that._createApprovalList(menuItemValue);
									this.destroy();
								}
							}, {
								text: Alfresco.util.message('button.no'),
								handler: function () {
									this.destroy();
								},
								isDefault: true
							}]
					});
				}

			}
		},

		printReportButton: null,
		editIterationButton : null,

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
							var prevSourceRouteInfo = this.sourceRouteInfo;

							this.stageType = response.json.stageType;
							this.stageItemType = response.json.stageItemType;
							this.routeType = response.json.routeType;
							this.currentIterationNode = response.json.currentIterationNode ? response.json.currentIterationNode : null;
							this.approvalState = response.json.approvalState;
							this.approvalResult = response.json.approvalResult;
							this.completedCurrentApprovalsCount = response.json.completedCurrentApprovalsCount;
							this.completedHistoryApprovalsCount = response.json.completedHistoryApprovalsCount;
							this.sourceRouteInfo = response.json.sourceRouteInfo;
							this.approvalIsEditable = response.json.approvalIsEditable;
							if (!this.approvalIsEditable) {
								YAHOO.util.Dom.setStyle(this.id + '-add-stage', "display", "none");
								YAHOO.util.Dom.setStyle(this.id + '-editIteration', "display", "none");
							} else {
								YAHOO.util.Dom.setStyle(this.id + '-add-stage', "display", "inline-block");
								YAHOO.util.Dom.setStyle(this.id +'-editIteration', "display", "table-cell");
							}

							this.approvalHistoryFolder = response.json.approvalHistoryFolder;

							if (prevSourceRouteInfo && prevSourceRouteInfo != this.sourceRouteInfo) {
								this.refreshSourceRoute();
							}

							LogicECM.module.Routes = LogicECM.module.Routes || {};
							LogicECM.module.Routes.Const = LogicECM.module.Routes.Const || {};
							LogicECM.module.Routes.Const.ROUTES_CONTAINER = LogicECM.module.Routes.Const.ROUTES_CONTAINER || {};
							LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageItemType = this.stageItemType;
							LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageType = this.stageType;
							LogicECM.module.Routes.Const.ROUTES_CONTAINER.routeType = this.routeType;

							if (YAHOO.lang.isFunction(callback)) {
								callback.apply(this, callbackArgsArr);
							}
							YAHOO.Bubbling.fire("redrawDocumentActions");
						}
					}
				},
				onFailure: {
					fn: function () {
						Alfresco.util.PopupManager.displayMessage(
							{
								text:  Alfresco.util.message('message.failure')
							}, YAHOO.util.Dom.get(this.id));
					},
					scope: this
				},
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
					useFilterByOrg: false,
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
				case 'previous' :
					this._createApprovalListFromPrevious();
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
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
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
						simpleDialog.dialog.setHeader(Alfresco.util.message('title.new.approval.list.from.route'));
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
							message = Alfresco.util.message('message.error.running.macros') + ' ' + macrosName;
							LogicECM.module.Base.Util.displayErrorMessageWithDetails(Alfresco.util.message('title.error.running.macros'), message, macrosScript);
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
						LogicECM.module.Base.Util.displayErrorMessageWithDetails(Alfresco.util.message('logicecm.base.error'), Alfresco.util.message('message.save.failure'), response.json.message);
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
				onFailure: {
					fn: function () {
						Alfresco.util.PopupManager.displayMessage(
							{
								text:  Alfresco.util.message('message.failure')
							}, YAHOO.util.Dom.get(this.id));
					},
					scope: this
				},
				execScripts: true,
				scope: this
			});
		},

		_createApprovalListFromPrevious: function (callback, callbackArgsArr) {
			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/routes/createIterationFromPrevious',
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
				onFailure: {
					fn: function () {
						Alfresco.util.PopupManager.displayMessage(
							{
								text:  Alfresco.util.message('message.failure')
							}, YAHOO.util.Dom.get(this.id));
					},
					scope: this
				},
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
					errorText = Alfresco.util.message('message.unable.to.edit.running.iteration');
					break;
				case 'COMPLETE':
					errorText = Alfresco.util.message('message.unable.to.edit.complete.iteration');
					break;
				case 'NOT_EXITS':
					this._createEmptyApprovalLst(this._showEditIterationDialog);
					break;
				default:
					this._showEditIterationDialog();
			}

			if (errorText) {
				Alfresco.util.PopupManager.displayPrompt({
					title: Alfresco.util.message('title.unable.to.edit'),
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
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
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
						simpleDialog.dialog.setHeader(Alfresco.util.message('label.routes.edit-route.title'));
						this.editItreationFormOpened = false;
						simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
							LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
							LogicECM.module.Base.Util.formDestructor(event, args, params);
						}, {moduleId: simpleDialog.id}, this);
					},
					scope: this
				},
				onSuccess: {
					fn: function () {
						Alfresco.util.PopupManager.displayMessage(
							{
								text: Alfresco.util.message("message.save.iteration.success")
							}, YAHOO.util.Dom.get(this.id));
					},
					scope: this
				},
				onFailure: {
					fn: function () {
						Alfresco.util.PopupManager.displayMessage(
							{
								text: Alfresco.util.message("message.save.iteration.fail")
							}, YAHOO.util.Dom.get(this.id));
					},
					scope: this
				}
			});

			editIterationForm.show();
		},

		onAddStageButton: function () {
			var createStageFunction = LogicECM.module.Routes.StagesControlDatagrid.prototype.onActionCreate;
			switch (this.approvalState) {
				case 'COMPLETE':
					Alfresco.util.PopupManager.displayPrompt({
						title: Alfresco.util.message('title.unable.to.add.stage'),
						text: Alfresco.util.message('message.unable.to.add.stage.iteration.completed')
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

		onActionEdit: function (item) {
			LogicECM.module.Routes.StagesControlDatagrid.prototype.onActionEdit.call(this, item);
		},

		onClearButton: function () {
			var that = this;
			Alfresco.util.PopupManager.displayPrompt({
				title: Alfresco.util.message('title.approval.list.clear'),
				text: Alfresco.util.message('message.clear.approval.list'),
				close: false,
				modal: true,
				buttons: [
					{
						text: Alfresco.util.message('button.yes'),
						handler: function () {
							that._deleteApprovalList();
							this.destroy();
						}
					}, {
						text: Alfresco.util.message('button.no'),
						handler: function () {
							this.destroy();
						},
						isDefault: true
					}]
			});
		},

        onExpandAllStages: function () {
            YAHOO.Bubbling.fire('expandAllGridRows', {
            	id: this.id
			});
        },

		fillCurrentApprovalState: function () {
			var approvalMsgTemplate = '{msg} ({additionalMsg})';
			this.completedApprovalsCountContainer.innerHTML = this.completedCurrentApprovalsCount + this.completedHistoryApprovalsCount;
			this.sourceRouteInfoContainer.innerHTML = this.sourceRouteInfo ? YAHOO.lang.substitute(approvalMsgTemplate, {
				msg: Alfresco.util.message('label.approval.typical'),
				additionalMsg: this.sourceRouteInfo
			}) : Alfresco.util.message('label.approval.individual');
			this.currentApprovalInfoContainer.innerHTML = this.approvalState === 'COMPLETE' ? YAHOO.lang.substitute(approvalMsgTemplate, {
				msg: this.approvalStateSettings[this.approvalState].stateMsg,
				additionalMsg: this.approvalResult.title
			}) : this.approvalStateSettings[this.approvalState].stateMsg;
		},

		manageControlsVisibility: function () {
			var approvalListButtonMenu, items, itemData, visible;
			function hide(element) {
				YAHOO.util.Dom.addClass(element, 'hidden');
			}

			function reveal(element) {
				YAHOO.util.Dom.removeClass(element, 'hidden');
			}

			function previous(element) {
				return 'previous' == element.value;
			}

			if (this.completedHistoryApprovalsCount > 0) {
				reveal(this.showHistoryLink);
			} else {
				hide(this.showHistoryLink);
			}

			this.approvalStateSettings[this.approvalState].hideElements.forEach(hide);
			this.approvalStateSettings[this.approvalState].revealElements.forEach(reveal);

			if (this.createApprovalListButton) {
				approvalListButtonMenu = this.createApprovalListButton.getMenu();
				items = approvalListButtonMenu.getItems().filter(previous);
				itemData = approvalListButtonMenu.itemData.filter(previous);
				visible = ('COMPLETE' == this.approvalState) || (this.completedHistoryApprovalsCount > 0);

				if (items.length) {
					if (visible) {
						reveal(items[0].element);
					} else {
						hide(items[0].element);
					}
				} else if (itemData.length) {
					if (visible) {
						itemData[0].classname = null;
					} else {
						itemData[0].classname = 'hidden';
					}
				}
			}
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
							onFailure: {
								fn: function () {
									Alfresco.util.PopupManager.displayMessage(
										{
											text:  Alfresco.util.message('message.failure')
										}, YAHOO.util.Dom.get(this.id));
								},
								scope: this
							}
						});
					},
					scope: this
				},
				onFailure: {
					fn: function () {
						Alfresco.util.PopupManager.displayMessage(
							{
								text:  Alfresco.util.message('message.failure')
							}, YAHOO.util.Dom.get(this.id));
					},
					scope: this
				}
			});
		},

		onHideControl: function (layer, args) {
			if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
				YAHOO.util.Dom.setStyle(this.id, "display", "none");
			}
		},

		onShowControl: function (layer, args) {
			if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
				YAHOO.util.Dom.setStyle(this.id, "display", "block");
			}
		},

		onShowHistoryButton: function () {
			var formId = 'showApprovalHistoryForm';
			var showApprovalHistoryForm = new Alfresco.module.SimpleDialog(this.id + '-' + formId);
			showApprovalHistoryForm.setOptions({
				width: '80em',
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
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
						simpleDialog.dialog.setHeader(Alfresco.util.message('title.approval.history'));
						simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
							LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
							LogicECM.module.Base.Util.formDestructor(event, args, params);
						}, {moduleId: simpleDialog.id}, this);
						form.setAJAXSubmit(false);
						simpleDialog.widgets.cancelButton.set('label', Alfresco.util.message('button.close'));
						simpleDialog.widgets.okButton.addClass('hidden');
					},
					scope: this
				},
				onFailure: {
					fn: function (response) {
						LogicECM.module.Base.Util.displayErrorMessageWithDetails(Alfresco.util.message('logicecm.base.error'), Alfresco.util.message('message.save.failure'), response.json.message);
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
		},
		onDataItemsDeleted: function DataGrid_onDataItemsDeleted(layer, args) {
			var obj = args[1], recordFound, el;

			if (obj && this._hasEventInterest(obj.bubblingLabel) && obj.items) {
				for (var i = 0, ii = obj.items.length; i < ii; i++) {
					recordFound = this._findRecordByParameter(obj.items[i].nodeRef, "nodeRef");
					if (recordFound) {
						el = this.widgets.dataTable.getTrEl(recordFound);
						Alfresco.util.Anim.fadeOut(el, {
							callback: function () {
								this.widgets.dataTable.deleteRow(recordFound);
							},
							scope: this
						});
					}
				}
			}
		},

		refreshSourceRoute: function () {
			if (this.currentIterationNode) {
				Alfresco.util.Ajax.jsonRequest({
					method: 'POST',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/routes/refreshSourceRoute',
					dataObj: {
						nodeRef: this.currentIterationNode
					},
					onFailure: {
						fn: function () {
							Alfresco.util.PopupManager.displayMessage(
								{
									text:  Alfresco.util.message('message.failure')
								}, YAHOO.util.Dom.get(this.id));
						},
						scope: this
					}
				});
			}
		},

		getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {
			function formatState(nodeRef, decisionData, hasComment) {
				if (!hasComment) {
					return null;
				}
				return YAHOO.lang.substitute("<a href=\'javascript:void(0);\' onclick=\'LogicECM.module.Base.Util.viewAttributes({config})\'>{displayValue}</a>", {
					config: YAHOO.lang.JSON.stringify({
						itemId: nodeRef,
						title: 'label.view.stage.details',
						formId: 'viewStageResult'
					}),
					displayValue: decisionData.displayValue
				});
			}
			var html = null;

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
					var datalistColumn = grid.datagridColumns[oColumn.key];
					if (datalistColumn) {
						oData = YAHOO.lang.isArray(oData) ? oData : [oData];
						for (var i = 0; i < oData.length; i++) {
							switch (datalistColumn.name) {
								case 'lecmApproveAspects:approvalState':
									var state = oRecord.getData('itemData')['prop_lecmApproveAspects_approvalState'];
									var hasComment = !!(oRecord.getData('itemData')['prop_lecmApproveAspects_hasComment'] && oRecord.getData('itemData')['prop_lecmApproveAspects_hasComment'].value);
									var nodeRef = oRecord.getData("nodeRef");
									html = formatState(nodeRef, state, hasComment);
									break;
								case 'lecmWorkflowRoutes:stageExpression':
									if (oData[i].displayValue && oData[i].displayValue.length) {
										html = '<div class="centered"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png" width="16" alt="true" title="true" id="yui-gen538"></div>';
									} else {
										html = '';
									}
									break;
								default:
									break;
							}
						}
					}
				}
			}
			return html;
		}
	}, true);

	LogicECM.module.Approval.StageExpanded.getCustomCellFormatter = function (grid, elCell, oRecord, oColumn, oData) {
		function formatState(nodeRef, decisionData, hasComment) {
			var commentIcon = '<img alt="' + Alfresco.util.message('label.comment') + '" src="' + Alfresco.constants.URL_RESCONTEXT + 'themes/lecmTheme/images/create-new-button.png">';
			if (decisionData.value === 'NO_DECISION') {
				return null;
			}
			return YAHOO.lang.substitute("<a href=\'javascript:void(0);\' onclick=\'LogicECM.module.Base.Util.viewAttributes({config})\'>{displayValue}</a>", {
				config: YAHOO.lang.JSON.stringify({
					itemId: nodeRef,
					title: 'label.view.approval.details',
					formId: 'viewApprovalResult'
				}),
				displayValue: decisionData.displayValue + (hasComment ? ' ' + commentIcon : '')
			});
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
