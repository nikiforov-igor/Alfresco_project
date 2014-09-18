if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};

(function() {

	LogicECM.module.Routes.StagesControlDatagrid = function(containerId) {
		LogicECM.module.Routes.StagesControlDatagrid.superclass.constructor.call(this, containerId);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Routes.StagesControlDatagrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Routes.StagesControlDatagrid.prototype, {
		onActionCreate: function(event, obj, isApprovalListContextProp) {
			function onCreateStageSuccess(r) {
				var formId = 'createStageForm';
				var createStageForm = new Alfresco.module.SimpleDialog(this.id + '-' + formId);
				var stageRef = r.json.nodeRef;

				createStageForm.setOptions({
					width: '50em',
					templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
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
						fn: function(form, simpleDialog) {
							var formNode = YAHOO.util.Dom.get(form.formId);
							var nameInput = YAHOO.util.Dom.getElementsBy(function(a) {
								return a.name.indexOf('cm_title') >= 0;
							}, 'input', formNode)[0];
							var recordsSize = this.widgets.dataTable.getRecordSet().getLength();

							if (nameInput) {
								nameInput.value = 'Этап ' + (recordsSize + 1);
							}

							simpleDialog.dialog.setHeader(this.msg('label.routes.create-stage.title'));
							this.createDialogOpening = false;
							simpleDialog.dialog.subscribe('destroy', function(event, args, params) {
								LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
								LogicECM.module.Base.Util.formDestructor(event, args, params);
							}, {moduleId: simpleDialog.id}, this);
						},
						scope: this
					},
					onSuccess: {
						fn: function(r) {
							var nodeRefObj = new Alfresco.util.NodeRef(stageRef);
							Alfresco.util.Ajax.jsonRequest({
								method: 'POST',
								url: Alfresco.constants.PROXY_URI_RELATIVE + 'slingshot/doclib/action/aspects/node/' + nodeRefObj.uri,
								dataObj: {
									added: [],
									removed: ['sys:temporary']
								},
								successCallback: {
									fn: function(r) {
										this.createDialogOpening = false;
										Alfresco.util.PopupManager.displayMessage({
											text: 'Этап успешно создан'
										});
									},
									scope: this
								},
								failureCallback: {
									fn: function(r) {
										this.createDialogOpening = false;
										Alfresco.util.PopupManager.displayMessage({
											text: 'Не удалось создать этап: ' + r.json.message
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
						fn: function(response) {
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
					fn: function(r) {
						this.editDialogOpening = false;
						Alfresco.util.PopupManager.displayMessage({
							text: 'Не удалось создать этап: ' + r.json.message
						});
					},
					scope: this
				}
			});

		},
		onActionEdit: function(item) {
			var formId = 'editStageForm';
			var editStageForm = new Alfresco.module.SimpleDialog(this.id + '-' + formId);

			editStageForm.setOptions({
				width: '50em',
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
				templateRequestParams: {
					itemId: item.nodeRef,
					itemKind: 'node',
					mode: 'edit',
					showCancelButton: true,
					submitType: 'json'
				},
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function(form, simpleDialog) {
						simpleDialog.dialog.setHeader(this.msg('label.routes.edit-stage.title'));
						this.createDialogOpening = false;
						simpleDialog.dialog.subscribe('destroy', function(event, args, params) {
							LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
							LogicECM.module.Base.Util.formDestructor(event, args, params);
						}, {moduleId: simpleDialog.id}, this);
					},
					scope: this
				},
				successMessage: 'Изменения в этапе сохранены',
				failureMessage: 'Не удалось сохранить изменения в этапе'
			});

			editStageForm.show();
		},
		onCollapse: function(record) {
			var expandedRow = YAHOO.util.Dom.get(this.getExpandedRecordId(record));
			LogicECM.module.Base.Util.destroyForm(this.getExpandedFormId(record));
			expandedRow.parentNode.removeChild(expandedRow);
		},
		_createNewStageItem: function(dialogType, destination) {
			var itemType = LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageItemType,
				createStageItemDialog = new Alfresco.module.SimpleDialog(this.id + '-createStageItemDialog'),
				formID, dialogHeader,
				actionUrl = Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/workflow/routes/CreateStageItemInQueue?interpolateMacros=' + this.options.isApprovalListContext,
				expandedBubblingLabel = destination.replace(/:|\//g, "_") + "-dtgrd"; // см. datagridId в stage-expanded.get.html.ftl

			switch (dialogType) {
				case 'employee' :
					formID = 'createNewStageItemForEmployee';
					dialogHeader = 'Добавить сотрудника в этап';
					break;
				case 'macros' :
					formID = 'createNewStageItemForMacros';
					dialogHeader = 'Добавить потенциального участника в этап';
					break;
				default :
					return;
					break;
			}

			createStageItemDialog.setOptions({
				width: '35em',
				templateUrl: 'lecm/components/form',
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
					fn: function(p_form, simpleDialog) {
						simpleDialog.dialog.setHeader(dialogHeader);
						simpleDialog.dialog.subscribe('destroy', function(event, args, params) {
							LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
							LogicECM.module.Base.Util.formDestructor(event, args, params);
						}, {moduleId: simpleDialog.id}, this);
					},
					scope: this
				},
				onSuccess: {
					fn: function(r) {
						var persistedObjects = r.json,
							persistedObjectsLength = persistedObjects.length,
							i, persistedObject;

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

						Alfresco.util.PopupManager.displayMessage({
							text: this.msg('message.new-row.success')
						});
					},
					scope: this
				},
				onFailure: {
					fn: function(response) {
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg('message.new-row.failure')
						});
					},
					scope: this
				}
			});
			createStageItemDialog.show();
		},
		onActionAddEmployee: function(item) {
			this._createNewStageItem('employee', item.nodeRef);
		},
		onActionAddMacros: function(item) {
			this._createNewStageItem('macros', item.nodeRef);
		}

	}, true);
})();
