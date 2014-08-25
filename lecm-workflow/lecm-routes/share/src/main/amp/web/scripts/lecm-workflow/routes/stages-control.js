if (typeof LogicECM == "undefined" || !LogicECM) {
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
		onActionCreate: function() {
			var formId = 'createStageForm';
			var createStageForm = new Alfresco.module.SimpleDialog(this.id + '-' + formId);
			var meta = this.datagridMeta;

			if (this.createDialogOpening) {
				return;
			}
			this.createDialogOpening = true;

			createStageForm.setOptions({
				width: '50em',
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
				templateRequestParams: {
					itemKind: "type",
					itemId: meta.itemType,
					destination: meta.nodeRef,
					mode: "create",
					submitType: "json",
					showCancelButton: true
				},
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function(form, simpleDialog) {
						var formNode = YAHOO.util.Dom.get(form.formId);
						var nameInput = YAHOO.util.Dom.getElementsBy(function(a) {
								return a.name.indexOf("cm_title") >= 0
							}, "input", formNode)[0];
						var recordsSize = this.widgets.dataTable.getRecordSet().getLength();

						if (nameInput) {
							nameInput.value = "Этап " + (recordsSize + 1);
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
					fn: function(response) {
						YAHOO.Bubbling.fire("nodeCreated", {
							nodeRef: response.json.persistedObject,
							bubblingLabel: this.options.bubblingLabel
						});
						YAHOO.Bubbling.fire("dataItemCreated", {
							nodeRef: response.json.persistedObject,
							bubblingLabel: this.options.bubblingLabel
						});
						Alfresco.util.PopupManager.displayMessage({
							text: "Этап создан"
						});

						this.createDialogOpening = false;
					},
					scope: this
				},
				onFailure: {
					fn: function(response) {
						this.displayErrorMessageWithDetails(this.msg("logicecm.base.error"), this.msg("message.save.failure"), response.json.message);
						this.createDialogOpening = false;
						this.widgets.cancelButton.set("disabled", false);
					},
					scope: this
				}
			});

			createStageForm.show();
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
				}
			});

			editStageForm.show();
		}

	}, true);
})();
