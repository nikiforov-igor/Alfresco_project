(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    LogicECM.module.DocumentsJournal.Toolbar = function (htmlId) {
        LogicECM.module.DocumentsJournal.Toolbar.superclass.constructor.call(this, "LogicECM.module.DocumentsJournal.Toolbar", htmlId, ["button", "container"]);
        this.toolbarButtons = {};
        // Decoupled event listeners
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
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
             * Кнопки Tollbara, активируются при выборе элемента в дереве
             * @constructor
             */
            toolbarButtons: null,

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

                if (searchTerm.length > 0) {
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
                    if (dataGrid.currentFilter) {
                        datagridMeta.searchConfig.filter = dataGrid.currentFilter;
                    }

                    dataGrid.search.performSearch({
                        searchConfig: datagridMeta.searchConfig,
                        searchShowInactive: dataGrid.options.searchShowInactive,
                        sort: datagridMeta.sort
                    });
                    YAHOO.Bubbling.fire("showFilteredLabel");
                } else {
                    datagridMeta.searchConfig = dataGrid.initialSearchConfig;
                    if (dataGrid.currentFilter != null) {
                        datagridMeta.searchConfig = YAHOO.lang.merge(datagridMeta.searchConfig, {filter: dataGrid.currentFilter});
                    }
                    if (datagridMeta.searchConfig.fullTextSearch) {
                        datagridMeta.searchConfig.fullTextSearch = null;
                    }

                    this.modules.dataGrid.search.performSearch({
                        parent: datagridMeta.nodeRef,
                        itemType: datagridMeta.itemType,
                        searchConfig: datagridMeta.searchConfig,
                        searchShowInactive: dataGrid.options.searchShowInactive
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
                    if (dataGrid.currentFilter != null) {
                        datagridMeta.searchConfig = YAHOO.lang.merge(datagridMeta.searchConfig, {filter: dataGrid.currentFilter});
                    }
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
            }
        }, true);
})();