if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};

(function() {

	LogicECM.module.Routes.StagesItemsControlDatagrid = function(containerId) {

		YAHOO.util.Event.onContentReady(containerId, function() {
			var addItemDropdown = YAHOO.util.Dom.get(this.id + '-add-item-dropdown');
			if (addItemDropdown) {
				YAHOO.util.Event.on(addItemDropdown, 'change', this.onAddItemDropdownChange, this, true);
			}
		}, this, true);

		return LogicECM.module.Routes.StagesItemsControlDatagrid.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Routes.StagesItemsControlDatagrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Routes.StagesItemsControlDatagrid.prototype, {
		onAddItemDropdownChange: function(event) {
			var dropdownMenu = event.target,
				dropdownMenuValue = dropdownMenu.value;

			switch (dropdownMenuValue) {
				case 'employee' :
				case 'macros' :
					this._createNewStageItem(dropdownMenuValue);
					break;
				default :
					break;
			}
			dropdownMenu.selectedIndex = 0;
		},
		_createNewStageItem: function(dialogType) {
			var dataGrid = this.modules.dataGrid,
				datagridMeta = dataGrid.datagridMeta,
				destination = datagridMeta.nodeRef,
				itemType = datagridMeta.itemType,
				createStageItemDialog = new Alfresco.module.SimpleDialog(this.id + '-createStageItemDialog'),
				formID, dialogHeader;

			switch (dialogType) {
				case 'employee' :
					formID = 'createNewStageItemForEmployee';
					dialogHeader = 'Добавить сотрудника в этап';
					break;
				case 'macros' :
					formID = 'createNewStageItemForMacros'
					dialogHeader = 'Добавить потенциального участника в этап';
					break;
				default :
					return;
					break;
			}


			createStageItemDialog.setOptions({
				width: '35em',
				templateUrl: 'lecm/components/form',
				actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/workflow/routes/CreateStageItemInQueue',
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
								bubblingLabel: this.options.bubblingLabel
							});
							YAHOO.Bubbling.fire('dataItemCreated', {
								nodeRef: persistedObject.nodeRef,
								bubblingLabel: this.options.bubblingLabel
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
		}
	}, true);
})();
