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
					width: "50em",
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
				var searchTerm = YAHOO.util.Dom.get(this.id + "-full-text-search").value;

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
                    datagridMeta.searchConfig.fullTextSearch = fullTextSearch;
                    datagridMeta.searchConfig.sort = "cm:name|true";
                    datagridMeta.searchConfig.formData = {
                        datatype:datagridMeta.itemType
                    };

                    YAHOO.Bubbling.fire("doSearch",
                        {
                            searchConfig:datagridMeta.searchConfig,
                            searchShowInactive:false,
                            bubblingLabel:dataGrid.options.bubblingLabel
                        });
                    YAHOO.Bubbling.fire("showFilteredLabel");
				} else {
                    datagridMeta.searchConfig = null;
                    YAHOO.Bubbling.fire("doSearch",
                        {
                            parent:datagridMeta.nodeRef,
                            itemType:datagridMeta.itemType,
                            searchConfig:null,
                            searchShowInactive:false,
                            bubblingLabel:me.options.bubblingLabel
                        });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
				}
			}
		},

		_onExSearchClick: function () {
			var scope = this;
			return function (event, obj) {
				var grid = scope.modules.dataGrid;
				var advSearch = grid.search;

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
			}

			Alfresco.util.createYUIButton(this, "searchButton", this._onSearchClick ());

			Alfresco.util.createYUIButton(this, "extendSearchButton", this._onExSearchClick ());

			var scope = this;
			var searchInput = YAHOO.util.Dom.get(this.id + "-full-text-search");
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
