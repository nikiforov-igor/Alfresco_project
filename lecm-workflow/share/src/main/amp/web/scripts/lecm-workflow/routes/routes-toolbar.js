if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};

(function() {

	LogicECM.module.Routes.Toolbar = function(containerId) {
		LogicECM.module.Routes.Toolbar.superclass.constructor.call(this, 'LogicECM.module.Routes.Toolbar', containerId, ['button', 'container', 'connection', 'json', 'selector']);
		this.setOptions({
			pageId: null,
			datagridBubblingLabel: null,
			inEngineer: false
		});
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Routes.Toolbar, Alfresco.component.Base, {
		_createNewRoute: function() {

			// Для предотвращения открытия нескольких карточек (при многократном быстром нажатии на кнопку создания)
			if (this.createDialogOpening) {
				return;
			}
			this.createDialogOpening = true;

			var dataGrid = LogicECM.module.Base.Util.findComponentByBubblingLabel('LogicECM.module.Base.DataGrid', this.options.datagridBubblingLabel);
			var itemType = dataGrid.datagridMeta.itemType;
			var destination = dataGrid.datagridMeta.nodeRef;

			var newRouteForm = new Alfresco.module.SimpleDialog(this.id + '-' + formId);

			newRouteForm.setOptions({
				width: '50em',
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
				templateRequestParams: {
					formId: 'createNewRouteForm',
					itemId: itemType,
					itemKind: 'type',
					mode: 'create',
					destination: destination,
					showCancelButton: true,
					submitType: 'json'
				},
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function(form, simpleDialog) {
						simpleDialog.dialog.setHeader(this.msg('label.routes.new-route.title'));
						this.createDialogOpening = false;
					},
					scope: this
				},
				onSuccess: {
					fn: function(response) {
						YAHOO.Bubbling.fire('dataItemCreated', {
							nodeRef: response.json.persistedObject,
							bubblingLabel: this.options.datagridBubblingLabel
						});

						Alfresco.util.PopupManager.displayMessage({
							text: 'Маршрут успешно создан'
						});
					},
					scope: this
				},
				onFailure: {
					fn: function(r) {
						Alfresco.util.PopupManager.displayMessage({
							text: 'Не удалось создать маршрут: ' + r.json.message
						});
					},
					scope: this
				}
			});

			newRouteForm.show();
		},
		onReady: function() {
			if (this.options.inEngineer) {
				Alfresco.util.createYUIButton(this, 'btnCreateNewRoute', this._createNewRoute(), {
					label: this.msg('button.new-route')
				});
			}

			YAHOO.util.Dom.setStyle(this.id + '-body', 'visibility', 'visible');

			Alfresco.logger.info('A new LogicECM.module.Routes.Toolbar has been created');
		}
	});
})();
