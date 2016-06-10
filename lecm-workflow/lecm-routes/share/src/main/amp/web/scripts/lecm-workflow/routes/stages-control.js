if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};

(function () {

	LogicECM.module.Routes.StagesControlDatagrid = function (containerId) {
		LogicECM.module.Routes.StagesControlDatagrid.superclass.constructor.call(this, containerId);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Routes.StagesControlDatagrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Routes.StagesControlDatagrid.prototype, {
		onActionCreate: function (event, obj, isApprovalListContextProp) {
			function onCreateStageSuccess(r) {
				var formId = this.options.createStageFormId;
				var createStageForm = new Alfresco.module.SimpleDialog(this.id + '-' + formId);
				var stageRef = r.json.nodeRef;

				createStageForm.setOptions({
					width: '50em',
					templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
					templateRequestParams: {
						formId: formId,
						itemId: stageRef,
						itemKind: 'node',
						mode: 'edit',
						showCancelButton: true,
						submitType: 'json',
						isApprovalListContext: isApprovalListContext
					},
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: function (form, simpleDialog) {
							var formNode = YAHOO.util.Dom.get(form.formId);
							var nameInput = YAHOO.util.Dom.getElementsBy(function (a) {
								return a.name.indexOf('cm_title') >= 0;
							}, 'input', formNode)[0];
							var recordsSize = this.widgets.dataTable.getRecordSet().getLength();

							if (nameInput) {
								nameInput.value = Alfresco.util.message('lecm.routers.stage') + ' ' + (recordsSize + 1);
							}

							simpleDialog.dialog.setHeader(this.msg('label.routes.create-stage.title'));
							this.createDialogOpening = false;
							simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
								LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
								LogicECM.module.Base.Util.formDestructor(event, args, params);
							}, {moduleId: simpleDialog.id}, this);
						},
						scope: this
					},
					onSuccess: {
						fn: function (r) {
							var nodeRefObj = new Alfresco.util.NodeRef(stageRef);
							Alfresco.util.Ajax.jsonRequest({
								method: 'POST',
								url: Alfresco.constants.PROXY_URI_RELATIVE + 'slingshot/doclib/action/aspects/node/' + nodeRefObj.uri,
								dataObj: {
									added: [],
									removed: ['sys:temporary']
								},
								successCallback: {
									fn: function (r) {
										this.createDialogOpening = false;
										Alfresco.util.PopupManager.displayMessage({
											text: Alfresco.util.message('lecm.routers.stage.created')
										});
									},
									scope: this
								},
								failureCallback: {
									fn: function (r) {
										this.createDialogOpening = false;
										Alfresco.util.PopupManager.displayMessage({
											text: Alfresco.util.message('lecm.routers.stage.create.failed') + ': ' + r.json.message
										});
									},
									scope: this
								}
							});

							YAHOO.Bubbling.fire('nodeCreated', {
								nodeRef: r.json.persistedObject,
								bubblingLabel: this.options.bubblingLabel
							});
							YAHOO.Bubbling.fire('dataItemCreated', {
								nodeRef: r.json.persistedObject,
								bubblingLabel: this.options.bubblingLabel
							});

						},
						scope: this
					},
					onFailure: {
						fn: function (response) {
							this.displayErrorMessageWithDetails(this.msg('logicecm.base.error'), this.msg('message.save.failure'), response.json.message);
							this.createDialogOpening = false;
							this.widgets.cancelButton.set('disabled', false);
						},
						scope: this
					}
				});

				createStageForm.show();
			}

			var itemType = this.datagridMeta.itemType;
			var destination = this.datagridMeta.nodeRef;
			var isApprovalListContext = !!isApprovalListContextProp;


			if (this.createDialogOpening) {
				return;
			}
			this.createDialogOpening = true;

			//делаем ajax-запрос на получение нового пустого этапа
			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/routes/createNewTemporaryNode',
				dataObj: {
					destination: destination,
					nodeType: itemType
				},
				successCallback: {
					fn: onCreateStageSuccess,
					scope: this
				},
				failureCallback: {
					fn: function (r) {
						this.editDialogOpening = false;
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.util.message('lecm.routers.stage.create.failed') + ': ' + r.json.message
						});
					},
					scope: this
				}
			});

		},
		onActionEdit: function (item) {
			var formId = this.options.createStageFormId;
			var editStageFormId = this.id + '-' + formId;
			var editStageForm = new Alfresco.module.SimpleDialog(editStageFormId);

			editStageForm.setOptions({
				width: '50em',
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
				templateRequestParams: {
					formId: formId,
					itemId: item.nodeRef,
					itemKind: 'node',
					mode: 'edit',
					showCancelButton: true,
					submitType: 'json'
				},
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function (form, simpleDialog) {
						simpleDialog.dialog.setHeader(this.msg('label.routes.edit-stage.title'));
						this.createDialogOpening = false;
						simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
							LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
							LogicECM.module.Base.Util.formDestructor(event, args, params);
						}, {moduleId: simpleDialog.id}, this);
					},
					scope: this
				},
				onSuccess: {
					fn: function (response) {
						// Спасаем тонущий popup
						Alfresco.util.PopupManager.zIndex = YAHOO.util.Dom.get(editStageFormId + '-form-container_c').style['z-index'] + 1;
						YAHOO.Bubbling.fire("datagridRefresh", {
							bubblingLabel:this.options.bubblingLabel
						});
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg('lecm.routers.stage.changes.saved')
						});
					},
					scope: this
				},
				onFailure: {
					fn: function (response) {
						// Спасаем тонущий popup
						Alfresco.util.PopupManager.zIndex = YAHOO.util.Dom.get(editStageFormId + '-form-container_c').style['z-index'] + 1;
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg('lecm.routers.stage.changes.failed')
						});
					},
					scope: this
				}
			});

			editStageForm.show();
		},
		onCollapse: function (record) {
			var expandedRow = YAHOO.util.Dom.get(this.getExpandedRecordId(record));
			LogicECM.module.Base.Util.destroyForm(this.getExpandedFormId(record));
			expandedRow.parentNode.removeChild(expandedRow);
		},
		_createNewStageItem: function (dialogType, destination) {
			var itemType = LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageItemType,
				createStageItemDialog = new Alfresco.module.SimpleDialog(this.id + '-createStageItemDialog'),
				formID, dialogHeader,
				actionUrl = Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/workflow/routes/CreateStageItemInQueue?resolveMacros=' + !!this.options.isApprovalListContext,
				expandedBubblingLabel = destination.replace(/:|\//g, "_") + "-dtgrd"; // см. datagridId в stage-expanded.get.html.ftl

			switch (dialogType) {
				case 'employee' :
					formID = 'createNewStageItemForEmployee';
					dialogHeader = Alfresco.util.message('lecm.routers.add.employee.to.stage');
					break;
				case 'macros' :
					formID = 'createNewStageItemForMacros';
					dialogHeader = Alfresco.util.message('lecm.routers.add.potential.participant.to.stage');
					break;
				default :
					return;
					break;
			}

			createStageItemDialog.setOptions({
				width: '35em',
				templateUrl: 'components/form',
				actionUrl: actionUrl,
				templateRequestParams: {
					itemKind: 'type',
					itemId: itemType,
					destination: destination,
					formId: formID,
					mode: 'create',
					submitType: 'json',
					showCancelButton: true
				},
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function (p_form, simpleDialog) {
						simpleDialog.dialog.setHeader(dialogHeader);
						simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
							LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
							LogicECM.module.Base.Util.formDestructor(event, args, params);
						}, {moduleId: simpleDialog.id}, this);
					},
					scope: this
				},
				onSuccess: {
					fn: function (r) {
						var persistedObjects = r.json,
							persistedObjectsLength = persistedObjects.length,
							i, persistedObject, message;

						for (i = 0; i < persistedObjectsLength; i++) {
							persistedObject = persistedObjects[i];
							YAHOO.Bubbling.fire('nodeCreated', {
								nodeRef: persistedObject.nodeRef,
								bubblingLabel: expandedBubblingLabel
							});
							YAHOO.Bubbling.fire('dataItemCreated', {
								nodeRef: persistedObject.nodeRef,
								bubblingLabel: expandedBubblingLabel
							});
						}

						if (persistedObjectsLength) {
							message = this.msg('message.new-row.success');
						} else {
							message = this.msg('message.macros.empty.result');
						}
						Alfresco.util.PopupManager.displayMessage({
							text: message
						});
					},
					scope: this
				},
				onFailure: {
					fn: function (response) {
						var errorMessage = response.json.message,
							macrosName, macrosScript, messageSplittedArr, message;

						if (!!this.options.isApprovalListContext) {
							messageSplittedArr = errorMessage.split(' | ');
							macrosName = messageSplittedArr.splice(0, 2)[1];
							macrosScript = messageSplittedArr.join(' | ');
							message = this.msg('message.error.running.macros') + ' ' + macrosName;
							this.displayErrorMessageWithDetails(this.msg('title.error.running.macros'), message, macrosScript);
						} else {
							Alfresco.util.PopupManager.displayMessage({
								text: this.msg('message.new-row.failure')
							});
						}
						createStageItemDialog.hide();
					},
					scope: this
				}
			});
			createStageItemDialog.show();
		},
		onActionAddEmployee: function (item) {
			this._createNewStageItem('employee', item.nodeRef);
		},
		onActionAddMacros: function (item) {
			this._createNewStageItem('macros', item.nodeRef);
		},
		getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {
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
							if (datalistColumn.name == "lecmWorkflowRoutes:stageExpression") {
								if (oData[i].displayValue && oData[i].displayValue.length) {
									html = '<div class="centered"><img src="/share/res/components/images/complete-16.png" width="16" alt="true" title="true" id="yui-gen538"></div>';
								} else {
									html = '';
								}
								break;
							}
						}
					}
				}
			}
			return html;
		}

	}, true);
})();
