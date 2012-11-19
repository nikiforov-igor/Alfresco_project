/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Delegation module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Delegation
 */
LogicECM.module.Delegation = LogicECM.module.Delegation || {};

/**
 * Delegation module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.Delegation.Toolbar
 */
(function () {

	LogicECM.module.Delegation.Toolbar = function (containerId) {
		return LogicECM.module.Delegation.Toolbar.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.Toolbar",
			containerId,
			["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.Toolbar, Alfresco.component.Base, {

		_createProcuracyBtnClick: function () {
			var scope = this;
			return function (e, p_obj) {
				//		_createProcuracyBtnClick: function (event) {
				Alfresco.util.PopupManager.displayMessage({
					text: "createProcuracyBtnClick"
				});

				var datagridMeta = scope.modules.dataGrid.datagridMeta;
				var destination = datagridMeta.nodeRef;
				var itemType = datagridMeta.itemType;

				// Intercept before dialog show
				var doBeforeDialogShow = function DataListToolbar_onNewRow_doBeforeDialogShow (p_form, p_dialog) {
					Alfresco.util.populateHTML (
						[ p_dialog.id + "-dialogTitle", scope.msg ("label.new-row.title") ],
						[ p_dialog.id + "-dialogHeader", scope.msg ("label.new-row.header") ]
						);
				};

				var url = "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&showCancelButton=true";
				var templateUrl = YAHOO.lang.substitute (Alfresco.constants.URL_SERVICECONTEXT + url, {
					itemKind: "type",
					itemId: itemType,
					destination: destination,
					mode: "create",
					submitType: "json"
				});

				// Using Forms Service, so always create new instance
				var createRow = new Alfresco.module.SimpleDialog (scope.id + "-createRow");

				createRow.setOptions ({
					width: "33em",
					templateUrl: templateUrl,
					actionUrl: null,
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: doBeforeDialogShow,
						scope: scope
					},
					onSuccess: {
						fn: function DataListToolbar_onNewRow_success (response) {
							YAHOO.Bubbling.fire ("dataItemCreated", {
								nodeRef: response.json.persistedObject
							});

							Alfresco.util.PopupManager.displayMessage ({
								text: scope.msg ("message.new-row.success")
							});
						},
						scope: scope
					},
					onFailure: {
						fn: function DataListToolbar_onNewRow_failure (response) {
							Alfresco.util.PopupManager.displayMessage ({
								text: scope.msg("message.new-row.failure")
							});
						},
						scope: scope
					}
				}).show ();
			}
		},

		_refreshProcuraciesBtnClick: function () {
			var scope = this;
			return function (event, obj) {
				var url = window.location.protocol + "//" + window.location.host + window.location.pathname;
				window.location.href = url;
			}
		},

		_searchProcuraciesBtnClick: function () {
			var scope = this;
			return function (event, obj) {
				if (scope.modules.dataGrid) {
					var searchTerm = YAHOO.util.Dom.get("full-text-search").value;

					var dataGrid = scope.modules.dataGrid;
					var datagridMeta = dataGrid.datagridMeta;

					if (searchTerm.length > 0) {
						var columns = dataGrid.datagridColumns;

						var fields = "";
						for (var i = 0; i < columns.length; i++) {
							if (columns[i].dataType == "text") {
								fields += columns[i].name + ",";
							}
						}
						if (fields.length > 1) {
							fields = fields.substring(0, fields.length - 1);
						}
						var fullTextSearch = {
							parentNodeRef:datagridMeta.nodeRef,
							fields:fields,
							searchTerm:searchTerm
						};
						if (!datagridMeta.searchConfig) {
							datagridMeta.searchConfig = {};
						}
						datagridMeta.searchConfig.filter = "";
						datagridMeta.searchConfig.fullTextSearch = YAHOO.lang.JSON.stringify(fullTextSearch);

						YAHOO.Bubbling.fire("activeGridChanged", {
							datagridMeta:datagridMeta
						});

						YAHOO.Bubbling.fire("showFilteredLabel");
					} else {
						var nodeRef = datagridMeta.nodeRef;
						if (!datagridMeta.searchConfig) {
							datagridMeta.searchConfig = {};
						}
						datagridMeta.searchConfig.filter = 'PARENT:"' + nodeRef + '"' + ' AND (NOT (ASPECT:"lecm-dic:aspect_active") OR lecm\\-dic:active:true)';
						datagridMeta.searchConfig.fullTextSearch = "";
						YAHOO.Bubbling.fire("activeGridChanged", {
							datagridMeta:datagridMeta
						});
						YAHOO.Bubbling.fire("hideFilteredLabel");
					}
				}
			}
		},

		_onToolbarReady: function () {
			var container = YAHOO.util.Dom.get (this.id);
			Alfresco.util.createYUIButton(container, "btnCreateProcuracy", this._createProcuracyBtnClick (), {
				label: "создать доверенность"
			});

			Alfresco.util.createYUIButton(container, "btnRefreshProcuracies", this._refreshProcuraciesBtnClick (), {
				label: "обновить"
			});

			Alfresco.util.createYUIButton(container, "searchButton", this._searchProcuraciesBtnClick ());

			var scope = this;
			var searchInput = YAHOO.util.Dom.get("full-text-search");
			new YAHOO.util.KeyListener (searchInput, {
				keys: 13
			}, {
				fn: scope._searchProcuraciesBtnClick (),
				scope: scope,
				correctScope: true
			}, "keydown").enable();
		},

		onReady: function () {

			Alfresco.logger.info ("A new LogicECM.module.Delegation.Toolbar has been created");

			// Reference to Data Grid component
			this.modules.dataGrid = Alfresco.util.ComponentManager.findFirst ("LogicECM.module.Base.DataGrid");

			this._onToolbarReady ();
			//			YAHOO.util.Event.onContentReady(this.id, this._onToolbarReady);
			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}
	});
})();
