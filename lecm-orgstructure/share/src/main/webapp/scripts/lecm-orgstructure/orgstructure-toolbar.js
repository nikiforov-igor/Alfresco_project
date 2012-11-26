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
            options:{
                bubblingLabel: null
            },

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
                this.modules.dataGrid = this.findGrid("LogicECM.module.Base.DataGrid", this.options.bubblingLabel);

                // Finally show the component body here to prevent UI artifacts on YUI button decoration
                Dom.setStyle(this.id + "-body", "visibility", "visible");
            },

            // Добавление новой ноды
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
                                if (successEvent){// вызов дополнительного события
                                    YAHOO.Bubbling.fire("" + successEvent,
                                        {
                                            nodeRef:response.json.persistedObject,
                                            bubblingLabel:toolbar.options.bubblingLabel
                                        });
                                }
                                YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
                                    {
                                        nodeRef:response.json.persistedObject,
                                        bubblingLabel:toolbar.options.bubblingLabel
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
                            fn:function GenerateElementName(form) { // сгенерировать имя перед сохранением
                                generateNodeName(form, pattern, ",", false);
                            },
                            scope:this
                        }

                    }).show();
            }, /**

            * New Row button click handler
             */
            onNewRow:function OrgstructureToolbar_onNewRow(e, p_obj) {
                var orgMetadata = this.modules.dataGrid.datagridMeta,
                    destination = orgMetadata.nodeRef,
                    itemType = orgMetadata.itemType,
                    namePattern = orgMetadata.custom != null ? orgMetadata.custom.namePattern : null;

                this._createNode(itemType, destination, namePattern, null, "message.new-row.success", "message.new-row.failure");
            },

            /**
             * Создание нового подразделения
             */
            onNewUnit:function OrgstructureToolbar_onNewUnit(e, p_obj) {
                var meta = this.modules.dataGrid.datagridMeta;
                if (meta != null && meta.nodeRef.indexOf(":") > 0) {
                    var destination = meta.nodeRef;
                    var itemType = meta.itemType;
                    var namePattern = meta.custom != null ? meta.custom.namePattern : null;
                    this._createNode(itemType, destination, namePattern, "nodeCreated", "message.new-unit.success", "message.new-unit.failure");
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
                var searchTerm = Dom.get("full-text-search").value;

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
                    datagridMeta.searchConfig.fullTextSearch = YAHOO.lang.JSON.stringify(fullTextSearch);

                    YAHOO.Bubbling.fire("activeGridChanged",
                        {
                            datagridMeta:datagridMeta,
                            bubblingLable:me.options.bubblingLabel
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
                            datagridMeta:datagridMeta,
                            bubblingLabel:me.options.bubblingLabel
                        });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
                }
            },

            // клик на Атрибутивном Поиске
            onExSearchClick:function OrgstructureToolbar_onExSearch() {
                var grid = this.modules.dataGrid;
                var advSearch = grid.modules.search;

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
            }
        }, true);
})();