if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
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
			function onCreateRouteSuccess(r) {
				var formId = 'createNewRouteForm';
				var routeRef = r.json.nodeRef;
				var newRouteForm = new Alfresco.module.SimpleDialog(this.id + '-' + formId);

				newRouteForm.setOptions({
					width: '50em',
					templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
					templateRequestParams: {
						formId: formId,
						itemId: routeRef,
						itemKind: 'node',
						mode: 'edit',
						showCancelButton: true,
						submitType: 'json',
						createStageFormId: 'createStageFormWithExpression'
					},
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: function(form, simpleDialog) {
							simpleDialog.dialog.setHeader(this.msg('label.routes.new-route.title'));
							this.createDialogOpening = false;
							simpleDialog.dialog.subscribe('destroy', function(event, args, params){
								LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
								LogicECM.module.Base.Util.formDestructor(event, args, params);
							}, {moduleId: simpleDialog.id}, this);
						},
						scope: this
					},
					onSuccess: {
						fn: function(r) {
							var nodeRefObj = new Alfresco.util.NodeRef(routeRef);
							Alfresco.util.Ajax.jsonRequest({
								method: 'POST',
								url: Alfresco.constants.PROXY_URI_RELATIVE + 'slingshot/doclib/action/aspects/node/' + nodeRefObj.uri,
								dataObj: {
									added: [],
									removed: ["sys:temporary"]
								},
								successCallback: {
									fn: function(r) {
										Alfresco.util.PopupManager.displayMessage({
											text: Alfresco.util.message('lecm.routers.route.created')
										});
									},
									scope: this
								},
								failureCallback: {
									fn: function(r) {
										this.editDialogOpening = false;
										Alfresco.util.PopupManager.displayMessage({
											text: Alfresco.util.message('lecm.routers.route.create.failed') + ': ' + r.json.message
										});
									},
									scope: this
								}
							});

							YAHOO.Bubbling.fire('dataItemCreated', {
								nodeRef: r.json.persistedObject,
								bubblingLabel: this.options.datagridBubblingLabel
							});

						},
						scope: this
					},
					onFailure: {
						fn: function(r) {
							Alfresco.util.PopupManager.displayMessage({
								text: Alfresco.util.message('lecm.routers.route.create.failed') + ': ' + r.json.message
							});
						},
						scope: this
					}
				});

				newRouteForm.show();
			}

			// Для предотвращения открытия нескольких карточек (при многократном быстром нажатии на кнопку создания)
			if (this.createDialogOpening) {
				return;
			}
			this.createDialogOpening = true;

			var dataGrid = LogicECM.module.Base.Util.findComponentByBubblingLabel('LogicECM.module.Base.DataGrid', this.options.datagridBubblingLabel);
			var itemType = dataGrid.datagridMeta.itemType;
			var destination = dataGrid.datagridMeta.nodeRef;

			//делаем ajax-запрос на получение нового пустого маршрута
			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/routes/createNewTemporaryNode',
				dataObj: {
					destination: destination,
					nodeType: itemType

				},
				successCallback: {
					fn: onCreateRouteSuccess,
					scope: this
				},
				failureCallback: {
					fn: function(r) {
						this.editDialogOpening = false;
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.util.message('lecm.routers.route.create.failed') + ': ' + r.json.message
						});
					},
					scope: this
				}
			});
		},
		onReady: function() {
			if (this.options.inEngineer) {
				Alfresco.util.createYUIButton(this, 'btnCreateNewRoute', this._createNewRoute, {
					label: this.msg('button.new-route')
				});
			}

			YAHOO.util.Dom.setStyle(this.id + '-body', 'visibility', 'visible');

			Alfresco.logger.info('A new LogicECM.module.Routes.Toolbar has been created');
		}
	});
})();
