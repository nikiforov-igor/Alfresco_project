if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};

(function() {

	LogicECM.module.Routes.Toolbar = function(containerId) {
		return  LogicECM.module.Routes.Toolbar.superclass.constructor.call(this, 'LogicECM.module.Routes.Toolbar', containerId, ['button', 'container', 'connection', 'json', 'selector']);
	};

	YAHOO.lang.extend(LogicECM.module.Routes.Toolbar, Alfresco.component.Base, {
		options: {
			pageId: null,
			datagridBubblingLabel: null,
			inEngineer: false
		},

		_createNewPrivateRoute: function() {
			return this._createNewRoute(false);
		},

		_createNewCommonRoute: function() {
			return this._createNewRoute(true);
		},

		_createNewRoute: function(isCommon) {
			var that = this;

			return function() {
				function onCreateEmptyRouteWebscriptSuccess(r) {
					var routeRef = r.json.routeRef;

					var dataGrid = LogicECM.module.Base.Util.findComponentByBubblingLabel('LogicECM.module.Base.DataGrid', this.options.datagridBubblingLabel);
					var itemType = dataGrid.datagridMeta.itemType;
					var formId = isCommon ? 'createNewCommonRouteForm' : 'createNewPrivateRouteForm';

					var newRouteForm = new Alfresco.module.SimpleDialog(that.id + '-' + formId);

					newRouteForm.setOptions({
						width: '50em',
						templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
						templateRequestParams: {
							formId: formId,
							itemId: routeRef,
							itemKind: 'node',
							mode: 'edit',
							showCancelButton: true,
							submitType: 'json'
						},
						destroyOnHide: true,
						doBeforeDialogShow: {
							fn: function(form, simpleDialog) {
								var titleId = isCommon ? 'label.routes.new-common-route.title' : 'label.routes.new-private-route.title';
								simpleDialog.dialog.setHeader(this.msg(titleId));
							},
							scope: that
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
							scope: that
						},
						onFailure: {
							fn: function(r) {
								Alfresco.util.PopupManager.displayMessage({
									text: 'Не удалось создать маршрут: ' + r.json.message
								});
							},
							scope: that
						}
					});

					newRouteForm.show();
				}

				var routeType = isCommon ? 'UNIT' : 'EMPLOYEE';

				Alfresco.util.Ajax.jsonRequest({
					method: 'GET',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/route/CreateEmptyRouteWebscript',
					dataObj: {
						routeType: routeType
					},
					successCallback: {
						fn: onCreateEmptyRouteWebscriptSuccess,
						scope: that
					},
					failureCallback: {
						fn: function(r) {
							Alfresco.util.PopupManager.displayMessage({
								text: 'Не удалось создать маршрут: ' + r.json.message
							});
						}
					}
				});
			};
		},

		onReady: function() {
			Alfresco.util.createYUIButton(this, 'btnCreateNewPrivateRoute', this._createNewPrivateRoute(), {
				label: this.msg('button.new-private-route')
			});

			if (this.options.inEngineer) {
				Alfresco.util.createYUIButton(this, 'btnCreateNewCommonRoute', this._createNewCommonRoute(), {
					label: this.msg('button.new-common-route')
				});
			}

			YAHOO.util.Dom.setStyle(this.id + '-body', 'visibility', 'visible');

			Alfresco.logger.info('A new LogicECM.module.Routes.Toolbar has been created');
		}
	});
})();
