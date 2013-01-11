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
 * @class LogicECM.module.BusinessJournal
 */
LogicECM.module.BusinessJournal = LogicECM.module.BusinessJournal || {};

/**
 * Displays a list of Toolbar
 *
 * @namespace LogicECM
 * @class LogicECM.module.BusinessJournal.Toolbar
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
     * @return {LogicECM.module.BusinessJournal.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.BusinessJournal.Toolbar = function (htmlId) {
        LogicECM.module.BusinessJournal.Toolbar.superclass.constructor.call(this, "LogicECM.module.BusinessJournal.Toolbar", htmlId, ["button", "container"]);
        this.toolbarButtons ={};
        // Decoupled event listeners
        YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.BusinessJournal.Toolbar, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.BusinessJournal.Toolbar.prototype,
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
             * Кнопки Toolbar, активируются при выборе элемента в дереве
             * @constructor
             */
            toolbarButtons: null,

            /**
             * Fired by YUI when parent element is available for scripting.
             *
             * @method onReady
             */
            onReady:function() {
                /*this.toolbarButtons.newRecordButton = Alfresco.util.createYUIButton(this, "newRecordButton", this.onNewRow,
                    {
                        disabled:true,
                        value:"create"
                    });*/

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

            /**
             * New Row button click handler
             */
            onNewRow:function() {
                var metadata = this.modules.dataGrid.datagridMeta,
                    destination = metadata.nodeRef,
                    itemType = metadata.itemType,
                    namePattern = metadata.custom != null ? metadata.custom.namePattern : null;
                this.modules.dataGrid.createDialogShow({itemType:itemType, nodeRef: destination}, null, namePattern);
            },

            onUserAccess:function(layer, args) {
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
            onInitDataGrid: function(layer, args) {
                var datagrid = args[1].datagrid;
                if ((!this.options.bubblingLabel || !datagrid.options.bubblingLabel) || this.options.bubblingLabel == datagrid.options.bubblingLabel){
                    this.modules.dataGrid = datagrid;
                }
            },

            // по нажатию на кнопку Поиск
            onSearchClick:function() {
                var searchTerm = Dom.get(this.id + "-full-text-search").value;

                var dataGrid = this.modules.dataGrid;
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
                    datagridMeta.searchConfig.fullTextSearch = fullTextSearch;
                    datagridMeta.searchConfig.sort = "cm:name|true";
                    datagridMeta.searchConfig.formData = {
                        datatype:datagridMeta.itemType
                    };
                    this.modules.dataGrid.search.performSearch({
                        searchConfig:datagridMeta.searchConfig,
                        searchShowInactive:dataGrid.options.searchShowInactive
                    });
                    YAHOO.Bubbling.fire("showFilteredLabel");
                } else {
                    datagridMeta.searchConfig = dataGrid.initialSearchConfig;
                    this.modules.dataGrid.search.performSearch({
                        parent:datagridMeta.nodeRef,
                        itemType:datagridMeta.itemType,
                        searchConfig:datagridMeta.searchConfig,
                        searchShowInactive:dataGrid.options.searchShowInactive
                    });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
                }
            },

            // клик на Атрибутивном Поиске
            onExSearchClick:function() {
                var grid = this.modules.dataGrid;
                var advSearch = grid.search;

                advSearch.showDialog(grid.datagridMeta);
            },

            /**
             * Скрывает кнопку поиска, если строка ввода пустая
             * @constructor
             */
            checkShowClearSearch: function () {
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
            onClearSearch: function () {
                Dom.get(this.id + "-full-text-search").value = "";
                if (this.modules.dataGrid) {
                    var dataGrid = this.modules.dataGrid;
                    var datagridMeta = dataGrid.datagridMeta;
                    datagridMeta.searchConfig = dataGrid.initialSearchConfig;
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