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
 * LogicECM Orgstructure module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.OrgStructure.OrgStructure
 */
LogicECM.module.Base = LogicECM.module.Base || {};

/**
 * Displays a list of Toolbar
 *
 * @namespace LogicECM
 * @class LogicECM.module.Base.Toolbar
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {LogicECM.module.Base.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.Base.Toolbar = function (name, htmlId) {
        LogicECM.module.Base.Toolbar.superclass.constructor.call(this, name ? name : "LogicECM.module.Base.Toolbar", htmlId, ["button", "container"]);
        this.toolbarButtons = {
            "defaultActive": [],
            "activeOnTreeNodeClick": [],
            "activeOnUnitClick": [],
            "activeOnParentTableClick": []
        };
        this.groupActions = {};
        // Decoupled event listeners
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("refreshButtonState", this.onRefreshButtonState, this);
        YAHOO.Bubbling.on("changeSearchState", this.onChangeSearchState, this);
        YAHOO.Bubbling.on("selectedItemsChanged", this.onSelectedItemsChanged, this);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.Base.Toolbar, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Base.Toolbar.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {
                bubblingLabel: null,
                searchButtonsType: 'defaultActive',
                newRowButtonType: 'defaultActive'
            },

            toolbarButtons: {
                "defaultActive": [],
                "activeOnTreeNodeClick": [],
                "activeOnUnitClick": [],
                "activeOnParentTableClick": []
            },

            groupActions: {},

            _initButtons: function () {
                this.toolbarButtons[this.options.newRowButtonType].push(
                    Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow,
                        {
                            disabled: this.options.newRowButtonType != 'defaultActive',
                            value: "create"
                        })
                );

                this.toolbarButtons[this.options.searchButtonsType].push(
                    Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick,
                        {
                            disabled: this.options.searchButtonsType != 'defaultActive'
                        })
                );

                this.toolbarButtons[this.options.searchButtonsType].push(
                    Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick,
                        {
                            disabled: this.options.searchButtonsType != 'defaultActive'
                        })
                );
            },
            /**
             * Fired by YUI when parent element is available for scripting.
             *
             * @method onReady
             */
            onReady: function BaseToolbar_onReady() {

                this._initButtons();
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

                if (this.options.searchButtonsType != 'defaultActive') {
                    Dom.setStyle(Dom.get(this.id + "-searchInput"), 'background', '#eeeeee');
                    Dom.get(this.id + "-full-text-search").setAttribute('disabled', true);
                    Dom.setStyle(Dom.get(this.id + "-full-text-search"), 'background', '#eeeeee');
                }

                // Finally show the component body here to prevent UI artifacts on YUI button decoration
                Dom.setStyle(this.id + "-body", "visibility", "visible");
            },

            /**
             * New Row button click handler
             */
            onNewRow: function BaseToolbar_onNewRow(e, p_obj) {
                var orgMetadata = this.modules.dataGrid.datagridMeta;
                if (orgMetadata != null && orgMetadata.nodeRef.indexOf(":") > 0) {
                    var destination = orgMetadata.nodeRef;
                    var itemType = orgMetadata.itemType;
                    this.modules.dataGrid.showCreateDialog({itemType: itemType, nodeRef: destination});
                }
            },

            // инициализация грида
            onInitDataGrid: function BaseToolbar_onInitDataGrid(layer, args) {
                var datagrid = args[1].datagrid;
                if ((!this.options.bubblingLabel || !datagrid.options.bubblingLabel) || this.options.bubblingLabel == datagrid.options.bubblingLabel) {
                    this.modules.dataGrid = datagrid;
                }
            },

            // по нажатию на кнопку Поиск
            onSearchClick: function BaseToolbar_onSearch() {
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
                    datagridMeta.sort = datagridMeta.sort ? datagridMeta.sort : "cm:modified|false";
                    if (datagridMeta.searchConfig.formData) {
                        if (typeof datagridMeta.searchConfig.formData == "string") {
                            datagridMeta.searchConfig.formData = YAHOO.lang.JSON.parse(datagridMeta.searchConfig.formData);

                        }
                        datagridMeta.searchConfig.formData.datatype = datagridMeta.itemType;
                    } else {
                        datagridMeta.searchConfig.formData = {
                            datatype: datagridMeta.itemType
                        };
                    }
                    this.modules.dataGrid.search.performSearch({
                        searchConfig: datagridMeta.searchConfig,
                        searchShowInactive: dataGrid.options.searchShowInactive,
                        sort: datagridMeta.sort
                    });
                    YAHOO.Bubbling.fire("showFilteredLabel");
                } else {
                    this.onClearSearch();
                }
            },

            // клик на Атрибутивном Поиске
            onExSearchClick: function BaseToolbar_onExSearch() {
                var grid = this.modules.dataGrid;
                var advSearch = grid.search;

                advSearch.showDialog(grid.datagridMeta);
            },

            onRefreshButtonState: function Tree_onRefreshButtonsState(layer, args) {
                var obj = args[1];
                var label = obj.bubblingLabel;
                var flag, buttons, button;
                if (this._hasEventInterest(label)) {
                    if (obj.enabledButtons) {
                        for (var enIndex in obj.enabledButtons) {
                            if (obj.enabledButtons.hasOwnProperty(enIndex)) {
                                flag = obj.enabledButtons[enIndex];
                                if (this.toolbarButtons.hasOwnProperty(flag)) {
                                    buttons = this.toolbarButtons[flag];
                                    for (var btnIndx in buttons) {
                                        if (buttons.hasOwnProperty(btnIndx)) {
                                            button = buttons[btnIndx];
                                            if (button != null) {
                                                button.set("disabled", LogicECM.module.OrgStructure.IS_ENGINEER ? false : true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (obj.disabledButtons) {
                        for (var disIndex in obj.disabledButtons) {
                            if (obj.disabledButtons.hasOwnProperty(disIndex)) {
                                flag = obj.disabledButtons[disIndex];
                                if (this.toolbarButtons.hasOwnProperty(flag)) {
                                    buttons = this.toolbarButtons[flag];
                                    for (var btnIndx in buttons) {
                                        button = buttons[btnIndx];
                                        if (button != null) {
                                            button.set("disabled", true);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (obj.buttons) {
                        for (var index in obj.buttons) {
                            if (obj.buttons.hasOwnProperty(index)) {
                                button = this._findToolbarButton(index);
                                if (button != null) {
                                    button.set("disabled", obj.buttons[index] == "disabled");
                                }
                            }
                        }
                    }
                }
            },

            onChangeSearchState: function Tree_ononChangeSearchState(layer, args) {
                var obj = args[1];
                var label = obj.bubblingLabel;
                if (this._hasEventInterest(label)) {
                    var searchButton = this._findToolbarButton("searchButton", this.options.searchButtonsType);
                    if (searchButton) {
                        if (searchButton.get("disabled")) {
                            Dom.setStyle(Dom.get(this.id + "-searchInput"), 'background', '#eeeeee');
                            Dom.get(this.id + "-full-text-search").setAttribute('disabled', true);
                            Dom.setStyle(Dom.get(this.id + "-full-text-search"), 'background', '#eeeeee');
                        } else {
                            Dom.setStyle(Dom.get(this.id + "-searchInput"), 'background', '');
                            Dom.get(this.id + "-full-text-search").removeAttribute('disabled', true);
                            Dom.setStyle(Dom.get(this.id + "-full-text-search"), 'background', '');
                        }
                    }
                }
            },

            _findToolbarButton: function (id, key) {
                var button, buttons;
                if (key && this.toolbarButtons.hasOwnProperty(key)) {
                    buttons = this.toolbarButtons[key];
                    for (var btnIndx in buttons) {
                        if (buttons.hasOwnProperty(btnIndx)) {
                            button = buttons[btnIndx];
                            if (button != null) {
                                if (button.get("id") == this.id + "-" + id) {
                                    return button;
                                }
                            }
                        }
                    }
                } else {
                    for (var index in this.toolbarButtons) {
                        if (this.toolbarButtons.hasOwnProperty(index)) {
                            buttons = this.toolbarButtons[index];
                            for (var btnIndx in buttons) {
                                if (buttons.hasOwnProperty(btnIndx)) {
                                    button = buttons[btnIndx];
                                    if (button != null) {
                                        if (button.get("id") == this.id + "-" + id) {
                                            return button;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return button;
            },

            /**
             * @return {boolean}
             */
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

                    //сбрасываем на значение по умолчанию
                    if (datagridMeta.searchConfig) {
                        // сбрасываем терм поиска
                        if (datagridMeta.searchConfig.fullTextSearch) {
                            if (typeof datagridMeta.searchConfig.fullTextSearch == "string") {
                                datagridMeta.searchConfig.fullTextSearch = YAHOO.lang.JSON.parse(datagridMeta.searchConfig.fullTextSearch);
                            }
                            datagridMeta.searchConfig.fullTextSearch.searchTerm = null;

                            //проверяем, заполнена ли форма (атрибутивный поиск)
                            if (datagridMeta.searchConfig.formData) {
                                if (typeof datagridMeta.searchConfig.formData == "string") {
                                    datagridMeta.searchConfig.formData = YAHOO.lang.JSON.parse(datagridMeta.searchConfig.formData);
                                }
                                var nProps = 0;
                                for (var key in datagridMeta.searchConfig.formData) {
                                    nProps++;
                                }
                                if (nProps <= 1) {
                                    datagridMeta.searchConfig.fullTextSearch = null;
                                }
                            } else {
                                datagridMeta.searchConfig.fullTextSearch = null;
                            }
                        }
                    }
                    this.modules.dataGrid.search.performSearch({
                        parent: datagridMeta.nodeRef,
                        itemType: datagridMeta.itemType,
                        searchConfig: datagridMeta.searchConfig,
                        searchShowInactive: dataGrid.options.searchShowInactive
                    });
                    if (!datagridMeta.searchConfig || !datagridMeta.searchConfig.fullTextSearch) {
                        YAHOO.Bubbling.fire("hideFilteredLabel");
                    }
                    this.checkShowClearSearch();
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
            }
        }, true);
})();