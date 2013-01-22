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
        this.archivePanel = null;
        // Decoupled event listeners
        YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("selectedItemsChanged", this.onSelectedItemsChanged, this);
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

            groupActions: {},

            archivePanel: null,

            /**
             * Fired by YUI when parent element is available for scripting.
             *
             * @method onReady
             */
            onReady:function() {
                this.toolbarButtons.searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick,
                    {
                        disabled: true
                    });

                this.toolbarButtons.exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick,
                    {
                        disabled: true
                    });

                this.toolbarButtons.deleteButton = Alfresco.util.createYUIButton(this, "archiveByDateButton", this.onArchiveRowsDialog,
                    {
                        disabled: true
                    });

                this.groupActions.archiveButton = Alfresco.util.createYUIButton(this, "archiveButton", this.onArchiveRows,
                    {
                        disabled: true
                    });

                this.groupActions.exportCsvButton = Alfresco.util.createYUIButton(this, "exportCsvButton", this.onExportCSV,
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
                    this.archivePanel = new LogicECM.module.BusinessJournal.ArchivePanel("toolbar-archivePanel", datagrid);
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
                    //сбрасываем на значение по умолчанию
                    datagridMeta.searchConfig = YAHOO.lang.merge({}, dataGrid.initialSearchConfig);
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
                    //сбрасываем на значение по умолчанию
                    datagridMeta.searchConfig = YAHOO.lang.merge({}, dataGrid.initialSearchConfig);
                    dataGrid.search.performSearch({
                        parent:datagridMeta.nodeRef,
                        itemType:datagridMeta.itemType,
                        searchConfig:datagridMeta.searchConfig,
                        searchShowInactive:dataGrid.options.searchShowInactive
                    });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
                    this.checkShowClearSearch();
                }
            },

            onArchiveRowsDialog:function Toolbar_onDeleteRow() {
                    if (this.archivePanel && this.archivePanel.panel) {
                        Dom.setStyle(this.archivePanel.id, "display", "block");
                        this.archivePanel.panel.show();
                    } else {
                        alert("Не удалось найти панель!");
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
            },

            onExportCSV: function(){
                var datagridMeta = this.modules.dataGrid.datagridMeta;
                var selectItems = this.modules.dataGrid.selectedItems;
                var sUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/datagrid/config/columns?itemType=" + encodeURIComponent(datagridMeta.itemType) + "&formId=export-fields";
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: sUrl,
                        successCallback:
                        {
                            fn: function(response){
                                var datagridColumns = response.json.columns;
                                var fields = "";
                                var items = "";
                                var columns = "";
                                for (var nodeIndex in datagridColumns) {
                                    fields += (fields.length > 0 ? "," : "") + encodeURIComponent(datagridColumns[nodeIndex].name);
                                    columns += (columns.length > 0 ? "," : "") + encodeURIComponent(datagridColumns[nodeIndex].label);
                                }
                                for (var item in selectItems) {
                                    if (selectItems[item]) {
                                        items += (items.length > 0 ? "," : "") + encodeURIComponent(item);
                                    }
                                }
                                document.location.href = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/export-csv"
                                    + "?fields=" + fields
                                    + "&datagridColumns=" + columns
                                    + "&selectedItems=" + items
                                    + "&fileName=business-journal";
                            },
                            scope: this
                        },
                        failureCallback:
                        {
                            fn: function() {alert("Failed to load webscript export CSV.")},
                            scope: this
                        }
                    });
            },

            onArchiveRows:function Toolbar_onDeleteRow() {
                var dataGrid = this.modules.dataGrid;
                if (dataGrid) {
                    // Get the function related to the clicked item
                    var fn = "onActionDelete";
                    if (fn && (typeof dataGrid[fn] == "function")) {
                        dataGrid[fn].call(dataGrid, dataGrid.getSelectedItems());
                    }
                }
            },
        }, true);
})();

(function () {
    var $html = Alfresco.util.encodeHTML;

    LogicECM.module.BusinessJournal.ArchivePanel = function (id, datagrid) {
        LogicECM.module.BusinessJournal.ArchivePanel.superclass.constructor.call(this, "LogicECM.module.BusinessJournal.ArchivePanel", id, ["button", "container", "json"]);
        this.panel = null;
        this.panelButtons = {};
        this.dataGrid = datagrid;

        YAHOO.Bubbling.on("hidePanel", this.onCancel, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.BusinessJournal.ArchivePanel, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.module.BusinessJournal.ArchivePanel.prototype,
        {
            panel: null,
            dataGrid: null,
            panelButtons: null,
            isReady: false,

            onReady: function () {
                this.panel = Alfresco.util.createYUIPanel(this.id,
                    {
                        width: "500px"
                    });
                this.panelButtons.archiveButton = Alfresco.util.createYUIButton(this, "archiveButton", this.onArchive, {});
                this.panelButtons.cancelButton = Alfresco.util.createYUIButton(this, "cancelButton", this.onCancel, {});
            },
            onArchive: function () {
                var dateValue = Dom.get("archiveDate").value;
                if (!isNaN(dateValue)) {
                    var timerShowLoadingMessage = null;
                    var loadingMessage = null;
                    var me = this;

                    var fnShowLoadingMessage = function nShowLoadingMessage() {
                        if (timerShowLoadingMessage) {
                            loadingMessage = Alfresco.util.PopupManager.displayMessage(
                                {
                                    displayTime:0,
                                    text:'<span class="wait">' + $html(this.msg("label.loading")) + '</span>',
                                    noEscape:true
                                });

                            if (YAHOO.env.ua.ie > 0) {
                                this.loadingMessageShowing = true;
                            }
                            else {
                                loadingMessage.showEvent.subscribe(function () {
                                    this.loadingMessageShowing = true;
                                }, this, true);
                            }
                        }
                    };

                    // Slow data webscript message
                    this.loadingMessageShowing = false;
                    timerShowLoadingMessage = YAHOO.lang.later(500, this, fnShowLoadingMessage);

                    var destroyLoaderMessage = function DataGrid__uDG_destroyLoaderMessage() {
                        if (timerShowLoadingMessage) {
                            // Stop the "slow loading" timed function
                            timerShowLoadingMessage.cancel();
                            timerShowLoadingMessage = null;
                        }
                        if (loadingMessage) {
                            if (this.loadingMessageShowing) {
                                // Safe to destroy
                                loadingMessage.destroy();
                                loadingMessage = null;
                            }
                            else {
                                // Wait and try again later. Scope doesn't get set correctly with "this"
                                YAHOO.lang.later(100, me, destroyLoaderMessage);
                            }
                        }
                    };

                    var sUrl = Alfresco.constants.PROXY_URI + "lecm/business-journal/api/record/archive";
                    Alfresco.util.Ajax.jsonPost(
                        {
                            url: sUrl,
                            dataObj: {
                                nodeRefs: [],
                                archiveOTDays:dateValue
                            },
                            successCallback: {
                                fn: function (response) {
                                    destroyLoaderMessage();
                                    this.onCancel();
                                    YAHOO.Bubbling.fire("dataItemsDeleted",{
                                        items:response.json.results,
                                        bubblingLabel:this.options.bubblingLabel
                                    });
                                    /*this.dataGrid.search.performSearch({
                                        searchConfig:this.dataGrid.initialSearchConfig,
                                        searchShowInactive:this.dataGrid.options.searchShowInactive
                                    });*/
                                },
                                scope: this
                            },
                            failureCallback: {
                                fn: function (response) {
                                    destroyLoaderMessage();
                                    alert("Failed to load webscript")
                                },
                                scope: this
                            }
                        });
                } else {
                    alert("Введите дату");
                }
            },
            onCancel: function (layer, args) {
                if (this.panel != null) {
                    this.panel.hide();
                    Dom.setStyle(this.id, "display", "none");
                }
            }
        }, true);
})();