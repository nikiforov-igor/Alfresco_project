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
 * LogicECM ContractsFake module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.ContractsFake
 */
LogicECM.module.ContractsFake = LogicECM.module.ContractsFake || {};

/**
 * Displays a list of Toolbar
 *
 * @namespace LogicECM
 * @class LogicECM.module.ContractsFake.Toolbar
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
     * @return {LogicECM.module.ContractsFake.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.ContractsFake.Toolbar = function (htmlId) {
        LogicECM.module.ContractsFake.Toolbar.superclass.constructor.call(this, "LogicECM.module.ContractsFake.Toolbar", htmlId, ["button", "container"]);
        this.treeSelectActions = {};
        this.toolbarButtons ={};
        // Decoupled event listeners
        YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("initActiveButton", this.onInitButton, this);
        YAHOO.Bubbling.on("selectedItemsChanged", this.onSelectedItemsChanged, this);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.ContractsFake.Toolbar, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.ContractsFake.Toolbar.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options:{
                bubblingLabel: null,
                searchActive: null
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
            onReady:function DataListToolbar_onReady() {
                this.toolbarButtons.newRowButton = Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow,
                    {
                        disabled:true,
                        value:"create"
                    });

                this.toolbarButtons.searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick,
                    {
                        disabled: true
                    });

                this.toolbarButtons.exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick,
                    {
                        disabled: true
                    });

                this.groupActions.deleteButton = Alfresco.util.createYUIButton(this, "deleteButton", this.onDeleteRow,
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
                if (this.options.searchActive != null && this.options.searchActive == "false") {
                    Dom.setStyle(Dom.get(this.id+"-searchInput"), 'background','#eeeeee');
                    Dom.get(this.id + "-full-text-search").setAttribute('disabled', true);
                    Dom.setStyle(Dom.get(this.id+"-full-text-search"), 'background','#eeeeee');
                }
                // Reference to Data Grid component
                this.modules.dataGrid = this.findGrid("LogicECM.module.Base.DataGrid", this.options.bubblingLabel);

                // Finally show the component body here to prevent UI artifacts on YUI button decoration
                Dom.setStyle(this.id + "-body", "visibility", "visible");
            },

            /**
             * New Row button click handler
             */
            onNewRow:function (e, p_obj) {
                var metadata = this.modules.dataGrid.datagridMeta,
                    destination = metadata.nodeRef,
                    itemType = metadata.itemType;
                this.modules.dataGrid.showCreateDialog({itemType:itemType, nodeRef: destination}, null);
            },

            // разблокировать кнопки согласно правам
            onUserAccess:function (layer, args) {
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
                if ((!this.options.bubblingLabel || !datagrid.options.bubblingLabel) || this.options.bubblingLabel == datagrid.options.bubblingLabel){
                    this.modules.dataGrid = datagrid;
                }
            },

            // по нажатию на кнопку Поиск
            onSearchClick:function () {
                var searchTerm = Dom.get(this.id + "-full-text-search").value;

                var dataGrid = this.modules.dataGrid;
                var datagridMeta = dataGrid.datagridMeta;

                var me = this;
                if (searchTerm.length > 0) {
                    var columns = dataGrid.datagridColumns;

                    var fields = dataGrid.getTextFields();
                    var fullTextSearch = {
                        parentNodeRef:datagridMeta.nodeRef,
                        fields:fields,
                        searchTerm:searchTerm
                    };
                    if (!datagridMeta.searchConfig) {
                        datagridMeta.searchConfig = {};
                    }
                    datagridMeta.searchConfig.fullTextSearch = fullTextSearch;
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
                    datagridMeta.searchConfig = dataGrid.initialSearchConfig;
                    if (datagridMeta.searchConfig.fullTextSearch) {
                        datagridMeta.searchConfig.fullTextSearch = null;
                    }
                    this.modules.dataGrid.search.performSearch({
                        parent:datagridMeta.nodeRef,
                        itemType:datagridMeta.itemType,
                        searchConfig: datagridMeta.searchConfig,
                        searchShowInactive:false
                    });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
                }
            },

            // клик на Атрибутивном Поиске
            onExSearchClick:function () {
                var grid = this.modules.dataGrid;
                var advSearch = grid.search;

                advSearch.showDialog(grid.datagridMeta);
            },

            // функция, возвращающая грид, имеющий тот же bubblingLabel, что и тулбар
            findGrid:function (p_sName, bubblingLabel) {
                var found = Alfresco.util.ComponentManager.find(
                    {
                        name:p_sName
                    });
                if (bubblingLabel) {
                    for (var i = 0, j = found.length; i < j; i++) {
                        var component = found[i];
                        if (typeof component == "object" && component.options.bubblingLabel) {
                            if (component.options.bubblingLabel == bubblingLabel) {
                                return component;
                            }
                        }
                    }
                } else {
                    return (typeof found[0] == "object" ? found[0] : null);
                }
                return null;
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
                Dom.setStyle(Dom.get(this.id+"-searchInput"), 'background','');
                Dom.get(this.id + "-full-text-search").removeAttribute('disabled',true);
                Dom.setStyle(Dom.get(this.id+"-full-text-search"), 'background','');


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
                            datagridMeta:datagridMeta
                        });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
                    this.checkShowClearSearch();
                }
            },
            /**
             * Удаление выбранного значения в dataGrid.
             * Появляется диалоговое окно с потверждением на удаление
             */
            onDeleteRow:function Toolbar_onDeleteRow() {
                var dataGrid = this.modules.dataGrid;
                if (dataGrid) {
                    // Get the function related to the clicked item
                    var fn = "onActionDelete";
                    if (fn && (typeof dataGrid[fn] == "function")) {
                        dataGrid[fn].call(dataGrid, dataGrid.getSelectedItems(),null,{fullDelete:true});
                    }
                }
            },
            onSelectedItemsChanged: function Toolbar_onSelectedItemsChanged(layer, args)
            {
                if (this.modules.dataGrid)
                {
                    var items = this.modules.dataGrid.getSelectedItems();
                    for (var index in this.groupActions)
                    {
                        if (this.groupActions.hasOwnProperty(index))
                        {
                            var action = this.groupActions[index];
                            action.set("disabled", (items.length === 0));
                        }
                    }
                }
            }
        }, true);
})();