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
LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

/**
 * Displays a list of Toolbar
 *
 * @namespace LogicECM
 * @class LogicECM.module.OrgStructure.Toolbar
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
     * @return {LogicECM.module.OrgStructure.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.OrgStructure.Toolbar = function (htmlId) {
        LogicECM.module.OrgStructure.Toolbar.superclass.constructor.call(this, "LogicECM.module.OrgStructure.Toolbar", htmlId, ["button", "container"]);
        this.toolbarButtons = {
            "defaultActive": [],
            "activeOnTreeNodeClick": [],
            "activeOnUnitClick": [],
            "activeOnParentTableClick": []
        };
        // Decoupled event listeners
        /*YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);*/
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("refreshButtonState", this.onRefreshButtonState, this);
        YAHOO.Bubbling.on("changeSearchState", this.onChangeSearchState, this);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.OrgStructure.Toolbar, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.OrgStructure.Toolbar.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options:{
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

            /**
             * Fired by YUI when parent element is available for scripting.
             *
             * @method onReady
             */
            onReady: function DataListToolbar_onReady() {

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

                this.toolbarButtons["defaultActive"].push(
                    Alfresco.util.createYUIButton(this, "structure", this.onStructureClick)
                );


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
            onNewRow: function OrgstructureToolbar_onNewRow(e, p_obj) {
                var orgMetadata = this.modules.dataGrid.datagridMeta;
                var toolbar = this;
                if (orgMetadata != null && orgMetadata.nodeRef.indexOf(":") > 0) {
                    var destination = orgMetadata.nodeRef;
                    var itemType = orgMetadata.itemType;
                    this.modules.dataGrid.showCreateDialog({itemType: itemType, nodeRef: destination});
                }
            },

            // инициализация грида
            onInitDataGrid: function OrgstructureToolbar_onInitDataGrid(layer, args) {
                var datagrid = args[1].datagrid;
                if ((!this.options.bubblingLabel || !datagrid.options.bubblingLabel) || this.options.bubblingLabel == datagrid.options.bubblingLabel){
                    this.modules.dataGrid = datagrid;
                }
            },

            // по нажатию на кнопку Поиск
            onSearchClick:function OrgstructureToolbar_onSearch() {
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
                    if (dataGrid.initialSearchConfig != null) {
                        datagridMeta.searchConfig = YAHOO.lang.merge({}, dataGrid.initialSearchConfig);
                    } else {
                        datagridMeta.searchConfig = null;
                    }
                    this.modules.dataGrid.search.performSearch({
                        parent:datagridMeta.nodeRef,
                        itemType:datagridMeta.itemType,
                        searchConfig:null,
                        searchShowInactive:false
                    });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
                }
            },

            // клик на Атрибутивном Поиске
            onExSearchClick:function OrgstructureToolbar_onExSearch() {
                var grid = this.modules.dataGrid;
                var advSearch = grid.search;

                advSearch.showDialog(grid.datagridMeta);
            },

            onStructureClick:function OrgstructureToolbar_onStructureClick() {

                window.open(Alfresco.constants.PROXY_URI + "/lecm/orgstructure/diagram", "Структура организации", "top=0,left=0,height=768,width=1024");
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
                    var searchButton = this._findToolbarButton("searchButton",this.options.searchButtonsType);
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
                var button,buttons;
                if (key && this.toolbarButtons.hasOwnProperty(key)) {
                    buttons = this.toolbarButtons[key];
                    for (var btnIndx in buttons) {
                        if (buttons.hasOwnProperty(btnIndx)){
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
                        if (this.toolbarButtons.hasOwnProperty(index)){
                            buttons = this.toolbarButtons[index];
                            for (var btnIndx in buttons) {
                                if (buttons.hasOwnProperty(btnIndx)){
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
                    //сбрасываем на значение по умолчанию
                    datagridMeta.searchConfig = dataGrid.initialSearchConfig != null ? YAHOO.lang.merge({}, dataGrid.initialSearchConfig) : null;
                    YAHOO.Bubbling.fire("activeGridChanged",
                        {
                            datagridMeta:datagridMeta
                        });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
                    this.checkShowClearSearch();
                }
            }
        }, true);
})();