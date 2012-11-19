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

        // Decoupled event listeners
        YAHOO.Bubbling.on("selectedItemsChanged", this.onSelectedItemsChanged, this);
        YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
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
            options:{},

            /**
             * Fired by YUI when parent element is available for scripting.
             *
             * @method onReady
             */
            onReady:function DataListToolbar_onReady() {
                this.widgets.newRowButton = Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow,
                    {
                        disabled:true,
                        value:"create"
                    });
                this.widgets.newUnitButton = Alfresco.util.createYUIButton(this, "newUnitButton", this.onNewUnit,
                    {
                        disabled:true,
                        value:"create"
                    });
                // Selected Items menu button
                this.widgets.selectedItems = Alfresco.util.createYUIButton(this, "selectedItems-button", this.onSelectedItems,
                    {
                        type:"menu",
                        menu:"selectedItems-menu",
                        lazyloadmenu:false,
                        disabled:true
                    });

                this.widgets.searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick,
                    {
                        disabled: true
                    });

                this.widgets.exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick,
                    {
                        disabled: true
                    });

                var me = this;
                var searchInput = Dom.get("full-text-search");
                new YAHOO.util.KeyListener(searchInput,
                    {
                        keys: 13
                    },
                    {
                        fn: me.onSearchClick,
                        scope: this,
                        correctScope: true
                    }, "keydown").enable();

                // Reference to Data Grid component
                this.modules.dataGrid = Alfresco.util.ComponentManager.findFirst("LogicECM.module.Base.DataGrid");

                // Reference to Tree component
                this.modules.tree = Alfresco.util.ComponentManager.findFirst("LogicECM.module.OrgStructure.Tree");

                // Finally show the component body here to prevent UI artifacts on YUI button decoration
                Dom.setStyle(this.id + "-body", "visibility", "visible");
            },

            _createNode:function (itemType, destination, pattern, successEvent, successMsg, failureMsg) {
                var toolbar = this;
                var doBeforeDialogShow = function DataListToolbar_onNewRow_doBeforeDialogShow(p_form, p_dialog) {
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-dialogTitle", this.msg("label.new-row.title") ]
                    );
                };

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&showCancelButton=true",
                    {
                        itemKind:"type",
                        itemId:itemType,
                        destination:destination,
                        mode:"create",
                        submitType:"json"
                    });

                // Using Forms Service, so always create new instance
                var createRow = new Alfresco.module.SimpleDialog("toolbar-createRow");

                createRow.setOptions(
                    {
                        width:"50em",
                        templateUrl:templateUrl,
                        actionUrl:null,
                        destroyOnHide:false,
                        doBeforeDialogShow:{
                            fn:doBeforeDialogShow,
                            scope:this
                        },
                        onSuccess:{
                            fn:function DataListToolbar_onNewRow_success(response) {
                                YAHOO.Bubbling.fire("" + successEvent,
                                    {
                                        nodeRef:response.json.persistedObject
                                    });

                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text:this.msg(successMsg)
                                    });
                            },
                            scope:this
                        },
                        onFailure:{
                            fn:function DataListToolbar_onNewRow_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text:this.msg(failureMsg)
                                    });
                            },
                            scope:this
                        },
                        doBeforeFormSubmit:{
                            fn:function GenerateElementName(form) {
                                generateNodeName(form, pattern, ",", false);
                            },
                            scope:this
                        }
                    }).show();
            }, /**

            * New Row button click handler
             *
             * @method onNewRow
             * @param e {object} DomEvent
             * @param p_obj {object} Object passed back from addListener method
             */
            onNewRow:function DataListToolbar_onNewRow(e, p_obj) {
                var orgMetadata = this.modules.dataGrid.datagridMeta,
                    destination = orgMetadata.nodeRef,
                    itemType = orgMetadata.itemType,
                    namePattern = orgMetadata.custom != null ? orgMetadata.custom.namePattern : null;

                this._createNode(itemType, destination, namePattern, "dataItemCreated", "message.new-row.success", "message.new-row.failure");
            },

            /**
             * New Row button click handler
             *
             * @method onNewRow
             * @param e {object} DomEvent
             * @param p_obj {object} Object passed back from addListener method
             */
            onNewUnit:function DataListToolbar_onNewRow(e, p_obj) {
                if (this.modules.tree != null) {
                    var selectedNode = this.modules.tree.selectedNode;
                    if (selectedNode != null) {
                        var destination = selectedNode.data.nodeRef;
                        var itemType = selectedNode.data.type;
                        var namePattern = selectedNode.data.namePattern;
                        this._createNode(itemType, destination, namePattern, "unitCreated", "message.new-unit.success", "message.new-unit.failure");
                    } else {
                        Alfresco.util.PopupManager.displayMessage(
                            {
                                text:this.msg("message.select-unit.info")
                            });
                    }
                }
            },
            /**
             * Selected Items button click handler
             *
             * @method onSelectedItems
             * @param sType {string} Event type, e.g. "click"
             * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
             * @param p_obj {object} Object passed back from subscribe method
             */
            onSelectedItems:function DataListToolbar_onSelectedItems(sType, aArgs, p_obj) {
                var domEvent = aArgs[0],
                    eventTarget = aArgs[1];

                // Check mandatory docList module is present
                var dataGrid = this.modules.dataGrid;
                if (dataGrid) {
                    // Get the function related to the clicked item
                    var fn = Alfresco.util.findEventClass(eventTarget);
                    if (fn && (typeof dataGrid[fn] == "function")) {
                        dataGrid[fn].call(dataGrid, dataGrid.getSelectedItems(), dataGrid.datagridMeta.actionsConfig, null);
                    }
                }

                Event.preventDefault(domEvent);
            },

            /**
             * Deselect currectly selected assets.
             *
             * @method onActionDeselectAll
             */
            onActionDeselectAll:function DataListToolbar_onActionDeselectAll() {
                this.modules.dataGrid.selectItems("selectNone");
            },

            /**
             * User Access event handler
             *
             * @method onUserAccess
             * @param layer {object} Event fired
             * @param args {array} Event parameters (depends on event type)
             */
            onUserAccess:function DataListToolbar_onUserAccess(layer, args) {
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

            /**
             * Selected Items Changed event handler.
             * Determines whether to enable or disable the multi-item action drop-down
             *
             * @method onSelectedItemsChanged
             * @param layer {object} Event fired
             * @param args {array} Event parameters (depends on event type)
             */
            onSelectedItemsChanged:function DataListToolbar_onSelectedItemsChanged(layer, args) {
                if (this.modules.dataGrid) {
                    var items = this.modules.dataGrid.getSelectedItems(), item,
                        userAccess = {}, itemAccess, index,
                        menuItems = this.widgets.selectedItems.getMenu().getItems(), menuItem,
                        actionPermissions, disabled,
                        i, ii;

                    // Check each item for user permissions
                    for (i = 0, ii = items.length; i < ii; i++) {
                        item = items[i];

                        // Required user access level - logical AND of each item's permissions
                        itemAccess = item.permissions.userAccess;
                        for (index in itemAccess) {
                            if (itemAccess.hasOwnProperty(index)) {
                                userAccess[index] = (userAccess[index] === undefined ? itemAccess[index] : userAccess[index] && itemAccess[index]);
                            }
                        }
                    }

                    // Now go through the menu items, setting the disabled flag appropriately
                    for (index in menuItems) {
                        if (menuItems.hasOwnProperty(index)) {
                            // Defaulting to enabled
                            menuItem = menuItems[index];
                            disabled = false;

                            if (menuItem.element.firstChild) {
                                // Check permissions required - stored in "rel" attribute in the DOM
                                if (menuItem.element.firstChild.rel && menuItem.element.firstChild.rel !== "") {
                                    // Comma-separated indicates and "AND" match
                                    actionPermissions = menuItem.element.firstChild.rel.split(",");
                                    for (i = 0, ii = actionPermissions.length; i < ii; i++) {
                                        // Disable if the user doesn't have ALL the permissions
                                        if (!userAccess[actionPermissions[i]]) {
                                            disabled = true;
                                            break;
                                        }
                                    }
                                }

                                menuItem.cfg.setProperty("disabled", disabled);
                            }
                        }
                    }
                    this.widgets.selectedItems.set("disabled", (items.length === 0));
                }
            },

            onInitDataGrid: function OrgstructureToolbar_onInitDataGrid(layer, args) {
                var datagrid = args[1].datagrid;
                this.modules.dataGrid = datagrid;
            },

            onSearchClick:function OrgstructureToolbar_onSearch() {
                if (this.modules.dataGrid) {
                    var searchTerm = Dom.get("full-text-search").value;

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
                        datagridMeta.searchConfig.filter = ""; // сбрасываем фильтр, так как поиск будет полнотекстовый
                        datagridMeta.searchConfig.fullTextSearch = YAHOO.lang.JSON.stringify(fullTextSearch);

                        YAHOO.Bubbling.fire("activeGridChanged",
                            {
                                datagridMeta:datagridMeta
                            });

                        YAHOO.Bubbling.fire("showFilteredLabel");
                    } else {
                        var nodeRef = datagridMeta.nodeRef;
                        if (!datagridMeta.searchConfig) {
                            datagridMeta.searchConfig = {};
                        }
                        datagridMeta.searchConfig.filter = 'PARENT:"' + nodeRef + '"' + ' AND (NOT (ASPECT:"lecm-dic:aspect_active") OR lecm\\-dic:active:true)';
                        datagridMeta.searchConfig.fullTextSearch = null;
                        YAHOO.Bubbling.fire("activeGridChanged",
                            {
                                datagridMeta:datagridMeta
                            });
                        YAHOO.Bubbling.fire("hideFilteredLabel");
                    }
                }
            },

            onExSearchClick:function OrgstructureToolbar_onExSearch() {
                if (this.modules.dataGrid) {
                    var grid = this.modules.dataGrid;
                    var advSearch = grid.modules.search;

                    advSearch.showDialog(grid.datagridMeta);
                }
            }
        }, true);
})();