if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};

(function() {

	LogicECM.module.Routes.Toolbar = function(containerId) {
		return  LogicECM.module.Routes.Toolbar.superclass.constructor.call(
				this,
				"LogicECM.module.Routes.Toolbar",
				containerId,
				["button", "container", "connection", "json", "selector"]);
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
			var scope = this;
			return function(event, obj) {
				var templateUrl, newRouteForm, formId,
						dataGrid = LogicECM.module.Base.Util.findComponentByBubblingLabel("LogicECM.module.Base.DataGrid", this.options.datagridBubblingLabel),
						destination = dataGrid.datagridMeta.nodeRef,
						itemType = dataGrid.datagridMeta.itemType,
						url = "lecm/components/form" +
						"?itemKind={itemKind}" +
						"&itemId={itemId}" +
						"&formId={formId}" +
						"&destination={destination}" +
						"&mode={mode}" +
						"&submitType={submitType}" +
						"&showCancelButton=true";

				formId = isCommon ? "createNewCommonRouteForm" : "createNewPrivateRouteForm";

				templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
					itemKind: "type",
					itemId: itemType,
					formId: formId,
					destination: destination,
					mode: "create",
					submitType: "json"
				});

				newRouteForm = new Alfresco.module.SimpleDialog(scope.id + "-" + formId);

				newRouteForm.setOptions({
					width: "50em",
					templateUrl: templateUrl,
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: function(p_form, p_dialog) {
							p_dialog.dialog.setHeader(this.msg(isCommon ? "label.routes.new-common-route.title" : "label.routes.new-private-route.title"));
						},
						scope: this
					},
					onSuccess: {
						fn: function(response) {
							YAHOO.Bubbling.fire("dataItemCreated", {
								nodeRef: response.json.persistedObject,
								bubblingLabel: this.options.datagridBubblingLabel
							});

							Alfresco.util.PopupManager.displayMessage({
								text: scope.msg("message.new-row.success")
							});
						},
						scope: scope
					},
					onFailure: {
						fn: function(response) {
							Alfresco.util.PopupManager.displayMessage({
								text: scope.msg("message.new-row.failure")
							});
						},
						scope: scope
					}
				});
				newRouteForm.show();
			};
		},
		onReady: function() {
			var scope = this;

			Alfresco.util.createYUIButton(this, "btnCreateNewPrivateRoute", this._createNewPrivateRoute(), {
				label: scope.msg("button.new-private-route")
			});

			if (this.options.inEngineer) {
				Alfresco.util.createYUIButton(this, "btnCreateNewCommonRoute", this._createNewCommonRoute(), {
					label: scope.msg("button.new-common-route")
				});
			}

			YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "visible");

			Alfresco.logger.info("A new LogicECM.module.Routes.Toolbar has been created");
		}
	});
})();
