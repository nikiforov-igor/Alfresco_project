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
        this.treeSelectActions = {};
        this.toolbarButtons ={};
        // Decoupled event listeners
        YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("initActiveButton", this.onInitButton, this);
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

            /**
             * Fired by YUI when parent element is available for scripting.
             *
             * @method onReady
             */
            onReady:function DataListToolbar_onReady() {
                var disable = false;
                if (this.options.searchActive == "false"){
                    disable = true;
                }
                this.widgets.newRowButton = Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow,
                    {
                        disabled:false,
                        value:"create"
                    });
                this.toolbarButtons.newUnitButton = Alfresco.util.createYUIButton(this, "newUnitButton", this.onNewUnit,
                    {
                        disabled:disable,
                        value:"create"
                    });

                this.toolbarButtons.searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick,
                    {
                        disabled: disable
                    });

                this.toolbarButtons.exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick,
                    {
                        disabled: disable
                    });
                this.treeSelectActions.newRowButtonStaff = Alfresco.util.createYUIButton(this, "newRowButtonStaff", this.onNewRow,
                    {
                        disabled:true,
                        value:"create"
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
            onNewRow:function OrgstructureToolbar_onNewRow(e, p_obj) {
                var orgMetadata = this.modules.dataGrid.datagridMeta,
                    destination = orgMetadata.nodeRef,
                    itemType = orgMetadata.itemType,
                    namePattern = orgMetadata.custom != null ? orgMetadata.custom.namePattern : null;
                this.modules.dataGrid.createDialogShow({itemType:itemType, nodeRef: destination}, null, namePattern);
            },

            /**
             * Создание нового подразделения
             */
            onNewUnit:function OrgstructureToolbar_onNewUnit(e, p_obj) {
                var meta = this.modules.dataGrid.datagridMeta;
                var toolbar = this;
                if (meta != null && meta.nodeRef.indexOf(":") > 0) {
                    var destination = meta.nodeRef;
                    var itemType = meta.itemType;
                    var namePattern = meta.custom != null ? meta.custom.namePattern : null;
                    var callBack = function(ref) {
                        YAHOO.Bubbling.fire("nodeCreated",
                            {
                                nodeRef:ref,
                                bubblingLabel:toolbar.options.bubblingLabel
                            });
                    };

                    this.modules.dataGrid.createDialogShow({itemType:itemType, nodeRef: destination}, callBack, namePattern);
                } else {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text:this.msg("message.select-unit.info")
                        });
                }
            },
            // разблокировать кнопки согласно правам
            onUserAccess:function OrgstructureToolbar_onUserAccess(layer, args) {
                var obj = args[1];
                if (obj && obj.userAccess) {
                    var widget, widgetPermissions, index, orPermissions, orMatch;
                    for (index in this.widgets) {
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

            // по нажатию на кнопку Поиск
            onSearchClick:function OrgstructureToolbar_onSearch() {
                var searchTerm = Dom.get(this.id + "-full-text-search").value;

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
                    datagridMeta.searchConfig.sort = "cm:name|true";
                    datagridMeta.searchConfig.formData = {
                        datatype:datagridMeta.itemType
                    };
                    this.modules.dataGrid.search.performSearch({
                        searchConfig:datagridMeta.searchConfig,
                        searchShowInactive:false
                    });
                    YAHOO.Bubbling.fire("showFilteredLabel");
                } else {
                    datagridMeta.searchConfig = null;
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

            // функция, возвращающая грид, имеющий тот же bubblingLabel, что и тулбар
            findGrid:function OrgstructureToolbar_findGrid(p_sName, bubblingLabel) {
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
                    datagridMeta.searchConfig = null;
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