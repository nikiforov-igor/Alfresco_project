(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    LogicECM.module.DocumentsJournal.Toolbar = function (htmlId) {
        LogicECM.module.DocumentsJournal.Toolbar.superclass.constructor.call(this, "LogicECM.module.DocumentsJournal.Toolbar", htmlId, ["button", "container"]);
        this.treeSelectActions = {};
        this.toolbarButtons = {};
        // Decoupled event listeners
        YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("selectedItemsChanged", this.onSelectedItemsChanged, this);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.DocumentsJournal.Toolbar, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.DocumentsJournal.Toolbar.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {
                bubblingLabel: null
            },
            /**
             * Дополнительные кнопки, активируемы при выборе элемента в дереве
             */
            treeSelectActions: null,

            /**
             * Кнопки Tollbara, активируются при выборе элемента в дереве
             * @constructor
             */
            toolbarButtons: null,

            groupActions: {},

            /**
             * Fired by YUI when parent element is available for scripting.
             *
             * @method onReady
             */
            onReady: function DataListToolbar_onReady() {
                this.toolbarButtons.searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick,
                    {
                        disabled: true
                    });

                this.toolbarButtons.exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick,
                    {
                        disabled: true
                    });

                var me = this;


                // Search
                this.checkShowClearSearch();
                Event.on(this.id + "-clearSearchInput", "click", this.onClearSearch, null, this);
                Event.on(this.id + "-full-text-search", "keyup", this.checkShowClearSearch, null, this);

                var searchInput = Dom.get(this.id + "-full-text-search");
                new YAHOO.util.KeyListener(searchInput,
                    {
                        keys: 13
                    },
                    {
                        fn: me.onSearchClick,
                        scope: this,
                        correctScope: true
                    }, "keydown").enable();


                // Finally show the component body here to prevent UI artifacts on YUI button decoration
                Dom.setStyle(this.id + "-body", "visibility", "visible");
            },

            // разблокировать кнопки согласно правам
            onUserAccess: function (layer, args) {
                var obj = args[1];
                if (obj && obj.userAccess) {
                    var widget, widgetPermissions, index, orPermissions, orMatch;
                    for (index in this.toolbarButtons) {
                        if (this.toolbarButtons.hasOwnProperty(index)) {
                            widget = this.toolbarButtons[index];
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
            onInitDataGrid: function (layer, args) {
                var datagrid = args[1].datagrid;
                if ((!this.options.bubblingLabel || !datagrid.options.bubblingLabel) || this.options.bubblingLabel == datagrid.options.bubblingLabel) {
                    this.modules.dataGrid = datagrid;
                }
            },

            // по нажатию на кнопку Поиск
            onSearchClick: function () {
                var searchTerm = Dom.get(this.id + "-full-text-search").value;

                var dataGrid = this.modules.dataGrid;
                var datagridMeta = dataGrid.datagridMeta;

                var me = this;
                if (searchTerm.length > 0) {
                    var columns = dataGrid.datagridColumns;

                    var fields = dataGrid.getTextFields();
                    var fullTextSearch = {
                        parentNodeRef: datagridMeta.nodeRef,
                        fields: fields,
                        searchTerm: searchTerm
                    };
                    if (!datagridMeta.searchConfig) {
                        datagridMeta.searchConfig = {};
                    }
                    datagridMeta.searchConfig.fullTextSearch = fullTextSearch;
                    datagridMeta.searchConfig.formData = {
                        datatype: datagridMeta.itemType
                    };
                    this.modules.dataGrid.search.performSearch({
                        searchConfig: datagridMeta.searchConfig,
                        searchShowInactive: false,
                        sort: datagridMeta.sort
                    });
                    YAHOO.Bubbling.fire("showFilteredLabel");
                } else {
                    datagridMeta.searchConfig = dataGrid.initialSearchConfig;
                    if (datagridMeta.searchConfig.fullTextSearch) {
                        datagridMeta.searchConfig.fullTextSearch = null;
                    }
                    this.modules.dataGrid.search.performSearch({
                        parent: datagridMeta.nodeRef,
                        itemType: datagridMeta.itemType,
                        searchConfig: datagridMeta.searchConfig,
                        searchShowInactive: false
                    });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
                }
            },

            // клик на Атрибутивном Поиске
            onExSearchClick: function () {
                var grid = this.modules.dataGrid;
                var advSearch = grid.search;

                advSearch.showDialog(grid.datagridMeta);
            },

            _hasEventInterest: function DataGrid_hasEventInterest(bubbleLabel) {
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
                if (Dom.get(this.id + "-full-text-search").value.length > 0) {
                    Dom.setStyle(this.id + "-clearSearchInput", "visibility", "visible");
                } else {
                    Dom.setStyle(this.id + "-clearSearchInput", "visibility", "hidden");
                }
            },

            /**
             * Очистка поиска
             * @constructor
             */
            onClearSearch: function Toolbar_onSearch() {
                Dom.get(this.id + "-full-text-search").value = "";
                if (this.modules.dataGrid) {
                    var dataGrid = this.modules.dataGrid;
                    var datagridMeta = dataGrid.datagridMeta;
                    datagridMeta.searchConfig = dataGrid.initialSearchConfig;
                    if (datagridMeta.searchConfig.fullTextSearch) {
                        datagridMeta.searchConfig.fullTextSearch = null;
                    }
                    YAHOO.Bubbling.fire("activeGridChanged",
                        {
                            datagridMeta: datagridMeta
                        });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
                    this.checkShowClearSearch();
                }
            },

            /**
             * Удаление выбранного значения в dataGrid.
             * Появляется диалоговое окно с потверждением на удаление
             */
            onDeleteRow: function Toolbar_onDeleteRow() {
                var dataGrid = this.modules.dataGrid;
                if (dataGrid) {
                    // Get the function related to the clicked item
                    var fn = "onActionDelete";
                    if (fn && (typeof dataGrid[fn] == "function")) {
                        dataGrid[fn].call(dataGrid, dataGrid.getSelectedItems(), null, {fullDelete: true});
                    }
                }
            },

            onSelectedItemsChanged: function Toolbar_onSelectedItemsChanged(layer, args) {
                if (this.modules.dataGrid) {
                    var items = this.modules.dataGrid.getSelectedItems();
                    for (var index in this.groupActions) {
                        if (this.groupActions.hasOwnProperty(index)) {
                            var action = this.groupActions[index];
                            action.set("disabled", (items.length === 0));
                        }
                    }
                }
            }
        }, true);
})();