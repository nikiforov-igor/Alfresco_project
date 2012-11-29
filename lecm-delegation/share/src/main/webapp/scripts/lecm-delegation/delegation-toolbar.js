if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


LogicECM.module.Delegation = LogicECM.module.Delegation || {};

(function () {

	LogicECM.module.Delegation.Toolbar = function (containerId) {
		return LogicECM.module.Delegation.Toolbar.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.Toolbar",
			containerId,
			["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.Toolbar, Alfresco.component.Base, {

		options: {
			pageId: null
		},

		_createProcuracyBtnClick: function () {
			var scope = this;
			return function (e, p_obj) {
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

		_createDelegationList: function () {
			var scope = this;
			return function (event, obj) {
				var datagridMeta = scope.modules.dataGrid.datagridMeta;
				var destination = datagridMeta.nodeRef;
				var itemType = datagridMeta.itemType;

				var url = "components/form"
							+ "?itemKind={itemKind}"
							+ "&itemId={itemId}"
							+ "&formId={formId}"
							+ "&destination={destination}"
							+ "&mode={mode}"
							+ "&submitType={submitType}"
							+ "&showCancelButton=true";
				var templateUrl = YAHOO.lang.substitute (Alfresco.constants.URL_SERVICECONTEXT + url, {
					itemKind: "type", //The "kind" of item the form is for, the only supported kind currently is "node".
					itemId: itemType, //The identifier of the item the form is for, this will be different for each "kind" of item, for "node" it will be a NodeRef.
					formId: "createDelegationOptsForm",//The form configuration to lookup, refers the id attribute of the form element. If omitted the default form i.e. the form element without an id attribute is used.
					destination: destination, //Provides a destination for any new items created by the form, when present a hidden field is generated with a name of alf_destination.
					mode: "create", //The mode the form will be rendered in, valid values are "view", "edit" and "create", the default is "edit".
					submitType: "json" //The "enctype" to use for the form submission, valid values are "multipart", "json" and "urlencoded", the default is "multipart".
				});

				// Using Forms Service, so always create new instance
				var delegationOptsForm = new Alfresco.module.SimpleDialog (scope.id + "-delegationOptsForm");

				delegationOptsForm.setOptions ({
					width: "33em",
					templateUrl: templateUrl,
					destroyOnHide: true,
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
				});
				delegationOptsForm.show ();
			}
		},

		_onSearchClick: function () {
			var scope = this;
			return function (event, obj) {
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
					datagridMeta.searchConfig.filter = ""; // сбрасываем фильтр, так как поиск будет полнотекстовый
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
					datagridMeta.searchConfig.fullTextSearch = null;
					YAHOO.Bubbling.fire("activeGridChanged", {
						datagridMeta:datagridMeta
					});
					YAHOO.Bubbling.fire("hideFilteredLabel");
				}
			}
		},

		_onExSearchClick: function () {
			var scope = this;
			return function (event, obj) {
				var grid = scope.modules.dataGrid;
				var advSearch = grid.modules.search;

				advSearch.showDialog(grid.datagridMeta);
			}
		},

		_onToolbarReady: function () {

			switch (this.options.pageId) {
				case "delegation-list":
					Alfresco.util.createYUIButton(this, "btnCreateDelegationList", this._createDelegationList (), {
						label: "создать параметры делегирования"
					});
					break;
				case "delegation-opts":
					break;
				case "delegation":
					Alfresco.util.createYUIButton(this, "btnCreateProcuracy", this._createProcuracyBtnClick (), {
						label: "создать доверенность"
					});

					Alfresco.util.createYUIButton(this, "btnRefreshProcuracies", this._refreshProcuraciesBtnClick (), {
						label: "обновить"
					});
					break;
			}

			Alfresco.util.createYUIButton(this, "searchButton", this._onSearchClick ());

			Alfresco.util.createYUIButton(this, "extendSearchButton", this._onExSearchClick ());

			var scope = this;
			var searchInput = YAHOO.util.Dom.get("full-text-search");
			new YAHOO.util.KeyListener (searchInput, {
				keys: 13
			}, {
				fn: scope._onSearchClick (),
				scope: scope,
				correctScope: true
			}, "keydown").enable();
		},

		onReady: function () {

			Alfresco.logger.info ("A new LogicECM.module.Delegation.Toolbar has been created");

			// Reference to Data Grid component
			this.modules.dataGrid = Alfresco.util.ComponentManager.findFirst ("LogicECM.module.Base.DataGrid");

			this._onToolbarReady ();
			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}
	});
})();
