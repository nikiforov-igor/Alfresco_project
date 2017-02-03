/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

LogicECM.module.Resolutions = LogicECM.module.Resolutions || {};

(function () {
    LogicECM.module.Resolutions.ExecutionTable = function (fieldHtmlId) {
        LogicECM.module.Resolutions.ExecutionTable.superclass.constructor.call(this, "LogicECM.module.Resolutions.ExecutionTable", fieldHtmlId, ["container", "datasource"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Resolutions.ExecutionTable, Alfresco.component.Base,
        {
            options: {
                documentNodeRef: null,
                jsonValue: null,
                datagridContainerId: null,
                datagridFormId: "datagrid",
                expandFormId: "expandFormId"
            },

            datagrid: null,

            onReady: function () {
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: Alfresco.constants.PROXY_URI + "/lecm/resolutions/prepareErrandsJson",
                        dataObj: {
                            nodeRef: this.options.documentNodeRef
                        },
                        successCallback: {
                            fn: function (response) {
                                if (response.json) {
                                    if (response.json.fromJson) {
                                        this.datagrid = new LogicECM.module.Resolutions.ExecutionJsonDataGrid(this.options.datagridContainerId).setOptions({
                                            usePagination: false,
                                            showExtendSearchBlock: false,
                                            actions: [],
                                            datagridMeta: {
                                                itemType: "lecm-errands:document",
                                                datagridFormId: this.options.datagridFormId
                                            },
                                            bubblingLabel: this.options.datagridContainerId,
                                            allowCreate: false,
                                            showActionColumn: false,
                                            showCheckboxColumn: false
                                        });
                                        this.datagrid.jsonData = response.json;
                                    } else {
                                        this.datagrid = new LogicECM.module.Base.DataGrid(this.options.datagridContainerId).setOptions({
                                            usePagination: false,
                                            showExtendSearchBlock: false,
                                            actions: [],
                                            expandable: true,
                                            expandDataObj: {
                                                formId: this.options.expandFormId
                                            },
                                            datagridMeta: {
                                                itemType: "lecm-errands:document",
                                                useChildQuery: false,
                                                useFilterByOrg: false,
                                                datagridFormId: this.options.datagridFormId,
                                                searchConfig: {
                                                    filter: "@lecm\\-errands\\:additional\\-document\\-assoc\\-ref:'" + this.options.documentNodeRef + "'"
                                                }
                                            },
                                            bubblingLabel: this.options.datagridContainerId,
                                            allowCreate: false,
                                            showActionColumn: false,
                                            showCheckboxColumn: false
                                        });
                                    }

                                    this.datagrid.setMessages(Alfresco.messages.scope["LogicECM.module.Resolutions.ExecutionTable"]);
                                    this.datagrid.draw();
                                }
                            },
                            scope: this
                        }
                    });
            }
        });


    LogicECM.module.Resolutions.ExecutionJsonDataGrid = function (htmlId) {
        return LogicECM.module.Resolutions.ExecutionJsonDataGrid.superclass.constructor.call(this, htmlId);
    };

    /**
     * Extend from LogicECM.module.Base.DataGrid
     */
    YAHOO.lang.extend(LogicECM.module.Resolutions.ExecutionJsonDataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Resolutions.ExecutionJsonDataGrid.prototype, {

        jsonData: null,

        _setupDataSource: function () {
            var datasource = new YAHOO.util.LocalDataSource(this.jsonData);
            datasource.responseType = YAHOO.util.DataSource.TYPE_JSON;
            datasource.responseSchema = {
                resultsList: "items",
                metaFields: {
                    startIndex: "startIndex",
                    totalRecords: "totalRecords",
                    isVersionable: "versionable",
                    meta: "metadata"
                }
            };

            return datasource;
        },

        _setupDataTable: function (columnDefinitions, me) {
            var dTable = new YAHOO.widget.DataTable(this.id + "-grid", columnDefinitions, this.widgets.dataSource,
                {
                    "MSG_EMPTY": this.msg("message.empty"),
                    "MSG_ERROR": this.msg("message.error")
                });

            // Enable row highlighting
            dTable.subscribe("rowMouseoverEvent", dTable.onEventHighlightRow, null, dTable);
            dTable.subscribe("rowMouseoutEvent", dTable.onEventUnhighlightRow, null, dTable);
            dTable.subscribe("rowHighlightEvent", this.onEventHighlightRow, this, true);
            dTable.subscribe("rowUnhighlightEvent", this.onEventUnhighlightRow, this, true);

            return dTable;
        },

        setupDataTable: function DataGrid__setupDataTable(columns) {
            var columnDefinitions = this.getDataTableColumnDefinitions();
            if (!this.widgets.dataTable) {
                this.widgets.dataTable = this._setupDataTable(columnDefinitions, this);
            }
        }
    }, true)
})();

