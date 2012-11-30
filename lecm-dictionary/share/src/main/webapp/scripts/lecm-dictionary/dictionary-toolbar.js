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
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Dictionary.Dictionary
 */
LogicECM.module.Dictionary = LogicECM.module.Dictionary || {};

/**
 * Data Lists: Toolbar component.
 *
 * Displays a list of Toolbar
 *
 * @namespace Alfresco
 * @class LogicECM.module.Dictionary.Toolbar
 */
(function()
{
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        UA = YAHOO.util.UserAction,
        Connect = YAHOO.util.Connect;

    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {LogicECM.module.Dictionary.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.Dictionary.Toolbar = function(htmlId)
    {
	    LogicECM.module.Dictionary.Toolbar.superclass.constructor.call(this, "LogicECM.module.Dictionary.Toolbar", htmlId, ["button", "container"]);

        // Decoupled event listeners
        YAHOO.Bubbling.on("selectedItemsChanged", this.onSelectedItemsChanged, this);
        YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);

        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.Dictionary.Toolbar, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Dictionary.Toolbar.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {},
            /**
             * FileUpload module instance.
             *
             * @property fileUpload
             * @type Alfresco.FileUpload
             */
            fileUpload: null,

            groupActions: {},
            panelCsv:null,
            panelXml: null,

            /**
             * Fired by YUI when parent element is available for scripting.
             *
             * @method onReady
             */
            onReady: function Toolbar_onReady()
            {
                this.widgets.newRowButton = Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow,
                    {
                        disabled: true,
                        value: "create"
                    });

                this.groupActions.exportCsvButton = Alfresco.util.createYUIButton(this, "exportCsvButton", this.onExportCSV,
                    {
                        disabled: true
                    });

                this.groupActions.deleteButton = Alfresco.util.createYUIButton(this, "deleteButton", this.onDeleteRow,
                    {
                        disabled: true
                    });

                this.widgets.importCsvButton = Alfresco.util.createYUIButton(this, "importCsvButton", function(){},
                    {
                        disabled:true
                    });
                this.widgets.searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearch,
                    {
                        disabled:true
                    });

                var me = this;

                // Import CSV
                var importCsvButton = this.widgets.importCsvButton;
                Event.on(this.id + "-import-csv-form-container", "mouseenter", function() {
                    UA.mouseover(importCsvButton);
                });
                Event.on(this.id + "-import-csv-form-container", "mouseleave", function() {
                    UA.mouseout(importCsvButton);
                });
                Event.on("import-csv-input", "change", function(){me.onImportCSV();});

	            // Search
	            this.checkShowClearSearch();
	            Event.on(this.id + "-clearSearchInput", "click", this.onClearSearch, null, this);
	            Event.on(this.id + "-searchInput", "keyup", this.checkShowClearSearch, null, this);

	            var searchInput = Dom.get(this.id + "-searchInput");
	            new YAHOO.util.KeyListener(searchInput,
		            {
			            keys: YAHOO.util.KeyListener.KEY.ENTER
		            },
		            {
			            fn: me.onSearch,
			            scope: this,
			            correctScope: true
		            }, "keydown").enable();


                // Reference to Data Grid component
                this.modules.dataGrid = Alfresco.util.ComponentManager.findFirst("LogicECM.module.Base.DataGrid");

                // Finally show the component body here to prevent UI artifacts on YUI button decoration
                Dom.setStyle(this.id + "-body", "visibility", "visible");
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
                        dataGrid[fn].call(dataGrid, dataGrid.getSelectedItems());
                    }
                }
            },

            /**
             * New Row button click handler
             *
             * @method onNewRow
             * @param e {object} DomEvent
             * @param p_obj {object} Object passed back from addListener method
             */
            onNewRow: function Toolbar_onNewRow(e, p_obj)
            {
                if (this.modules.dataGrid) {
                var datagridMeta = this.modules.dataGrid.datagridMeta,
                    destination = datagridMeta.nodeRef,
                    itemType = datagridMeta.itemType;

                // Intercept before dialog show
                var doBeforeDialogShow = function Toolbar_onNewRow_doBeforeDialogShow(p_form, p_dialog)
                {
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-dialogTitle", this.msg("label.new-row.title") ],
                        [ p_dialog.id + "-dialogHeader", this.msg("label.new-row.header") ]
                    );
                };

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&showCancelButton=true",
                    {
                        itemKind: "type",
                        itemId: itemType,
                        destination: destination,
                        mode: "create",
                        submitType: "json"
                    });

                // Using Forms Service, so always create new instance
                var createRow = new Alfresco.module.SimpleDialog(this.id + "-createRow");

                createRow.setOptions(
                    {
                        width: "33em",
                        templateUrl: templateUrl,
                        actionUrl: null,
                        destroyOnHide: true,
                        doBeforeDialogShow:
                        {
                            fn: doBeforeDialogShow,
                            scope: this
                        },
                        onSuccess:
                        {
                            fn: function Toolbar_onNewRow_success(response)
                            {
                                YAHOO.Bubbling.fire("dataItemCreated",
                                    {
                                        nodeRef: response.json.persistedObject
                                    });

                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.new-row.success")
                                    });
                            },
                            scope: this
                        },
                        onFailure:
                        {
                            fn: function Toolbar_onNewRow_failure(response)
                            {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.new-row.failure")
                                    });
                            },
                            scope: this
                        }
                    }).show();
                }
            },

            /**
             * User Access event handler
             *
             * @method onUserAccess
             * @param layer {object} Event fired
             * @param args {array} Event parameters (depends on event type)
             */
            onUserAccess: function Toolbar_onUserAccess(layer, args)
            {
                var obj = args[1];
                if (obj && obj.userAccess)
                {
                    var widget, widgetPermissions, index, orPermissions, orMatch;
                    for (index in this.widgets)
                    {
                        if (this.widgets.hasOwnProperty(index))
                        {
                            widget = this.widgets[index];
                            // Skip if this action specifies "no-access-check"
                            if (widget && widget.get("srcelement").className != "no-access-check")
                            {
                                // Default to disabled: must be enabled via permission
                                widget.set("disabled", false);
                                if (typeof widget.get("value") == "string")
                                {
                                    // Comma-separation indicates "AND"
                                    widgetPermissions = widget.get("value").split(",");
                                    for (var i = 0, ii = widgetPermissions.length; i < ii; i++)
                                    {
                                        // Pipe-separation is a special case and indicates an "OR" match. The matched permission is stored in "activePermission" on the widget.
                                        if (widgetPermissions[i].indexOf("|") !== -1)
                                        {
                                            orMatch = false;
                                            orPermissions = widgetPermissions[i].split("|");
                                            for (var j = 0, jj = orPermissions.length; j < jj; j++)
                                            {
                                                if (obj.userAccess[orPermissions[j]])
                                                {
                                                    orMatch = true;
                                                    widget.set("activePermission", orPermissions[j], true);
                                                    break;
                                                }
                                            }
                                            if (!orMatch)
                                            {
                                                widget.set("disabled", true);
                                                break;
                                            }
                                        }
                                        else if (!obj.userAccess[widgetPermissions[i]])
                                        {
                                            widget.set("disabled", true);
                                            break;
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
            /**
             * Экспорт CSV
             */
            onExportCSV: function(){
                var datagridMeta = this.modules.dataGrid.datagridMeta;
                var selectItems = this.modules.dataGrid.selectedItems;
                var datagridColumns = this.modules.dataGrid.datagridColumns;
                var sUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/dictionary/columns?itemType=" + encodeURIComponent(datagridMeta.itemType);
                var fields = "";
                var items = "";
                var columns = "";
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: sUrl,
                        successCallback:
                        {
                            fn: function(response){
                                var oResults = eval("(" + response.serverResponse.responseText + ")");
                                for (var nodeIndex in oResults) {
                                    fields += "field=" + encodeURIComponent(oResults[nodeIndex].fild) + "&";
                                    columns += "datagridColumns=" + encodeURIComponent(datagridColumns[nodeIndex].label) + "&";
                                }
                                for (var item in selectItems) {
                                    items += "selectedItems=" + encodeURIComponent(item) + "&";
                                }
                                document.location.href = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/export-csv"
                                                     + "?" + fields
                                                     + items + columns;
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
            /**
             * Поиск
             * @constructor
             */
            onSearch:function Toolbar_onSearch() {
                if (this.modules.dataGrid) {
                    var searchTerm = Dom.get(this.id + "-searchInput").value;

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
                        datagridMeta.searchConfig.filter = "";
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
                        datagridMeta.searchConfig.filter = 'PARENT:"' + nodeRef + '"';
                        datagridMeta.searchConfig.fullTextSearch = "";
                        YAHOO.Bubbling.fire("activeGridChanged",
                            {
                                datagridMeta:datagridMeta
                            });
                        YAHOO.Bubbling.fire("hideFilteredLabel");
                    }
                }
            },

	        /**
	         * Очистка поиска
	         * @constructor
	         */
			onClearSearch: function Toolbar_onSearch() {
		        Dom.get(this.id + "-searchInput").value = "";
				if (this.modules.dataGrid) {
					var dataGrid = this.modules.dataGrid;
					var datagridMeta = dataGrid.datagridMeta;
					if (!datagridMeta.searchConfig) {
						datagridMeta.searchConfig = {};
					}
					datagridMeta.searchConfig.filter = 'PARENT:"' + datagridMeta.nodeRef + '"';
					datagridMeta.searchConfig.fullTextSearch = "";
					YAHOO.Bubbling.fire("activeGridChanged",
						{
							datagridMeta:datagridMeta
						});
					YAHOO.Bubbling.fire("hideFilteredLabel");
					this.checkShowClearSearch();
				}
			},

	        /**
	         * Скрывает кнопку поиска, если строка ввода пустая
	         * @constructor
	         */
	        checkShowClearSearch: function Toolbar_checkShowClearSearch() {
		        if (Dom.get(this.id + "-searchInput").value.length > 0) {
			        Dom.setStyle(this.id + "-clearSearchInput", "visibility", "visible");
		        } else {
			        Dom.setStyle(this.id + "-clearSearchInput", "visibility", "hidden");
		        }
	        },

            /**
             * Метод вызываемый из другого скрипта для передачи параметров
             * @param layer {object} Event fired
             * @param args {array} Event parameters (depends on event type)
             * @constructor
             */
            onInitDataGrid:function Toolbar_onInitDataGrid(layer, args) {
                this.modules.dataGrid = args[1].datagrid;
            },
            /**
             * Импорт CSV
             */
            onImportCSV: function() {
                document.getElementById('nodeRef').value = this.modules.dataGrid.datagridMeta.nodeRef;
                Connect.setForm('import-csv-form', true);
                var url = Alfresco.constants.URL_CONTEXT + "proxy/alfresco/lecm/dictionary/post/import-csv";
                var fileUploadCallback = {
                    upload:function(o){
                        console.log('Server Response: ' + o.responseText);
                        document.location.reload(true);
                    }
                };
                Connect.asyncRequest(Alfresco.util.Ajax.GET, url, fileUploadCallback);
            }
        }, true);
})();