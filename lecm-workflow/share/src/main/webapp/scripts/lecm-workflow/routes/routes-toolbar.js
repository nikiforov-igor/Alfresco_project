if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};

(function() {

	var __datagridId__ = null;
	var __defferedCenterDialog__ = null;

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
		_onNewRouteFormDestroyed: function(event, args) {
			YAHOO.Bubbling.unsubscribe('assigneesListDatagridReady', this._onAssigneesListDatagridReady, this);
			YAHOO.Bubbling.unsubscribe('formContainerDestroyed', this._onNewRouteFormDestroyed, this);
			__defferedCenterDialog__.expire();
		},

		_onAssigneesListDatagridReady: function(event, args) {
			var bubblingLabel = args[1].bubblingLabel;
			if (!__datagridId__) {
				__datagridId__ = bubblingLabel;
				__defferedCenterDialog__.fulfil('datagrid1');
			} else if(__datagridId__ != bubblingLabel) {
				__datagridId__ = null;
				__defferedCenterDialog__.fulfil('datagrid2');
			} else {
				__datagridId__ = null;
			}
		},

		_centerDialog: function() {
			var newRouteForm = this.newRouteForm;
			var centerDialogInternal = function() {
				if (newRouteForm) {
					newRouteForm.dialog.center();
				}
			};
			setTimeout(centerDialogInternal, 50);
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
								this.createDialogOpening = false;
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
					this.newRouteForm = newRouteForm;
				}

				// Для предотвращения открытия нескольких карточек (при многократном быстром нажатии на кнопку создания)
				if (this.createDialogOpening) {
					return;
				}
				this.createDialogOpening = true;

				YAHOO.Bubbling.on('assigneesListDatagridReady', this._onAssigneesListDatagridReady, this);
				YAHOO.Bubbling.on('formContainerDestroyed', this._onNewRouteFormDestroyed, this);
				__defferedCenterDialog__ = new Alfresco.util.Deferred(['datagrid1', 'datagrid2'], {
					fn: this._centerDialog,
					scope: this
				});

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
