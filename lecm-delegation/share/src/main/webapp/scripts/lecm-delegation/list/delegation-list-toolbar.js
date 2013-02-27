if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.List = LogicECM.module.Delegation.List || {};

(function () {

	LogicECM.module.Delegation.List.Toolbar = function (containerId) {
		LogicECM.module.Delegation.List.Toolbar.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.List.Toolbar",
			containerId,
			["button", "container"]);
        YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("initActiveButton", this.onInitButton, this);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.List.Toolbar, Alfresco.component.Base, {

		// разблокировать кнопки согласно правам
		onUserAccess:function OrgstructureToolbar_onUserAccess(layer, args) {
			var obj = args[1];
			var searchActive = this.options.searchActive;
			if (obj && obj.userAccess) {
				var widget, widgetPermissions, index, orPermissions, orMatch;
				for (index in this.widgets) {
					// если задан параметр searchActive = false то кнопки поиска разблокируем.
						if (this.widgets.hasOwnProperty(index)) {
							widget = this.widgets[index];
							if (widget != null) {
								// Skip if this action specifies "no-access-check"
								if (widget.get("srcelement").className != "no-access-check") {
									// Default to disabled: must be enabled via permission
									widget.set("disabled", false);
									if (typeof widget.get("value") == "string") {
										// Comma-separation indicates "AND"
										widgetPermissions = widget.get("value").split(",");
										for (var i = 0, ii = widgetPermissions.length; i < ii; i++) {
											// Pipe-separation is a special case and indicates an "OR" match. The matched permission is stored in "activePermission" on the widget.
											if (widgetPermissions[i].indexOf("|") !== -1) {
												orMatch = false;
												orPermissions = widgetPermissions[i].split("|");
												for (var j = 0, jj = orPermissions.length; j < jj; j++) {
													if (obj.userAccess[orPermissions[j]]) {
														orMatch = true;
														widget.set("activePermission", orPermissions[j], true);
														break;
													}
												}
												if (!orMatch) {
													widget.set("disabled", true);
													break;
												}
											}
											else if (!obj.userAccess[widgetPermissions[i]]) {
												widget.set("disabled", true);
												break;
											}
										}
									}
								}
							}
						}
				}
			}
		},

		// инициализация грида
		onInitDataGrid: function OrgstructureToolbar_onInitDataGrid(layer, args) {
			var datagrid = args[1].datagrid;
			if ((!this.options.bubblingLabel || !datagrid.options.bubblingLabel) || this.options.bubblingLabel == datagrid.options.bubblingLabel){
				this.modules.dataGrid = datagrid;
			}
		},

		_onSearchClick: function () {
			var searchTerm = YAHOO.util.Dom.get(this.id + "-full-text-search").value;

			var dataGrid = this.modules.dataGrid;
			var datagridMeta = dataGrid.datagridMeta;

			var me = this;
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
				datagridMeta.sort = "cm:name|true";
				datagridMeta.searchConfig.formData = {
					datatype:datagridMeta.itemType
				};
				this.modules.dataGrid.search.performSearch({
					searchConfig:datagridMeta.searchConfig,
					searchShowInactive:false,
                    sort:datagridMeta.sort
				});
				YAHOO.Bubbling.fire("showFilteredLabel");
			} else {
				//сбрасываем на значение по умолчанию
				datagridMeta.searchConfig = dataGrid.initialSearchConfig != null ? YAHOO.lang.merge({}, dataGrid.initialSearchConfig) : null
				this.modules.dataGrid.search.performSearch({
					parent:datagridMeta.nodeRef,
					itemType:datagridMeta.itemType,
					searchConfig:null,
					searchShowInactive:false
				});
				YAHOO.Bubbling.fire("hideFilteredLabel");
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

			var disable = false;
			if (this.options.searchActive == "false"){
				disable = true;
			}

			Alfresco.util.createYUIButton(this, "searchButton", this._onSearchClick, {
				disabled: disable
            });

			Alfresco.util.createYUIButton(this, "extendSearchButton", this._onExSearchClick (), {
				disabled: disable
            });

			var me = this;


			// Search
			this.checkShowClearSearch();
			YAHOO.util.Event.on(this.id + "-clearSearchInput", "click", this.onClearSearch, null, this);
			YAHOO.util.Event.on(this.id + "-full-text-search", "keyup", this.checkShowClearSearch, null, this);
			var searchInput = YAHOO.util.Dom.get(this.id + "-full-text-search");
			new YAHOO.util.KeyListener(searchInput,
				{
					keys: 13
				},
				{
					fn: me._onSearchClick,
					scope: this,
					correctScope: true
				}, "keydown").enable();
			if (this.options.searchActive != null && this.options.searchActive == "false") {
				YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get(this.id+"-searchInput"), 'background','#eeeeee');
				YAHOO.util.Dom.get(this.id + "-full-text-search").setAttribute('disabled', true);
				YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get(this.id+"-full-text-search"), 'background','#eeeeee');
			}
		},

            onInitButton: function Tree_onSelectedItems(layer, args)
            {
                var obj = args[1];
                var label = obj.bubblingLabel;
                if(this._hasEventInterest(label)){
                    if (this.treeSelectActions != null) {
                        for (var index in this.treeSelectActions)
                        {
                            if (this.treeSelectActions.hasOwnProperty(index))
                            {
                                var action = this.treeSelectActions[index];
                                if (action != null) {
                                    action.set("disabled", args[1].disable);
                                }
                            }
                        }
                    }
                }
                if (this.options.searchActive == "false"){
                    if (this.toolbarButtons != null) {
                        for (var index in this.toolbarButtons)
                        {
                            if (this.toolbarButtons.hasOwnProperty(index))
                            {
                                var action = this.toolbarButtons[index];
                                if (action != null) {
                                    action.set("disabled", false);
                                }
                            }
                        }
                }
                }
                YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get(this.id+"-searchInput"), 'background','');
                YAHOO.util.Dom.get(this.id + "-full-text-search").removeAttribute('disabled',true);
                YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get(this.id+"-full-text-search"), 'background','');


            },

            _hasEventInterest: function DataGrid_hasEventInterest(bubbleLabel){
                if (!this.options.bubblingLabel || !bubbleLabel) {
                    return true;
                } else {
                    return this.options.bubblingLabel == bubbleLabel;
                }
            },
            /**
             * Скрывает кнопку поиска, если строка ввода пустая
             * @constructor
             */
            checkShowClearSearch: function Toolbar_checkShowClearSearch() {
                if (YAHOO.util.Dom.get(this.id + "-full-text-search").value.length > 0) {
                    YAHOO.util.Dom.setStyle(this.id + "-clearSearchInput", "visibility", "visible");
                } else {
                    YAHOO.util.Dom.setStyle(this.id + "-clearSearchInput", "visibility", "hidden");
                }
            },
            /**
             * Очистка поиска
             * @constructor
             */
            onClearSearch: function Toolbar_onSearch() {
                YAHOO.util.Dom.get(this.id + "-full-text-search").value = "";
                if (this.modules.dataGrid) {
                    var dataGrid = this.modules.dataGrid;
                    var datagridMeta = dataGrid.datagridMeta;
                    //сбрасываем на значение по умолчанию
                    datagridMeta.searchConfig = dataGrid.initialSearchConfig != null ? YAHOO.lang.merge({}, dataGrid.initialSearchConfig) : null
                    YAHOO.Bubbling.fire("activeGridChanged",
                        {
                            datagridMeta:datagridMeta
                        });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
                    this.checkShowClearSearch();
                }
            },

		onReady: function () {

			Alfresco.logger.info ("New LogicECM.module.Delegation.List.Toolbar has been created");

			this._onToolbarReady ();
			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}
	});
})();
