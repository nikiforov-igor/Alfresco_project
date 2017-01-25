if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};

(function() {

	LogicECM.module.Routes.DataGrid = function(containerId) {
		LogicECM.module.Routes.DataGrid.superclass.constructor.call(this, containerId);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Routes.DataGrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Routes.DataGrid.prototype, {
		onActionEdit: function (item) {
				var formId = 'editRouteForm';
				var editRouteForm = new Alfresco.module.SimpleDialog(this.id + '-' + formId);

				editRouteForm.setOptions({
					width: '50em',
					templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
					templateRequestParams: {
						formId: formId,
						itemId: item.nodeRef,
						itemKind: 'node',
						mode: 'edit',
						showCancelButton: true,
						submitType: 'json',
						createStageFormId: 'createStageFormWithExpression',
						showCaption: false
					},
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: function(form, simpleDialog) {
							simpleDialog.dialog.setHeader(this.msg('label.routes.edit-route.title'));
							this.createDialogOpening = false;
							simpleDialog.dialog.subscribe('destroy', function(event, args, params){
								LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
								LogicECM.module.Base.Util.formDestructor(event, args, params);
							}, {moduleId: simpleDialog.id}, this);
						},
						scope: this
					},
					successMessage: Alfresco.util.message('lecm.routers.route.changes.saved'),
					failureMessage: Alfresco.util.message('lecm.routers.route.changes.failed')
				});

				editRouteForm.show();
			}

	}, true);
})();
