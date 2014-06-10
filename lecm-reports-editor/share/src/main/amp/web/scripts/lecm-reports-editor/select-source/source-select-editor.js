/**
 * Module Namespaces
 */
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.ReportsEditor = LogicECM.module.ReportsEditor|| {};

/**
 * ReportsEditor module.
 *
 * @namespace LogicECM.module.ReportsEditor
 * @class LogicECM.module.ReportsEditor.SelectSourceEditor
 */
(function () {
    LogicECM.module.ReportsEditor.SelectSourceEditor = function (htmlId) {
        LogicECM.module.ReportsEditor.SelectSourceEditor.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.SelectSourceEditor",
            htmlId,
            ["button", "container", "connection"]);

        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("selectDataSource", this._onSelectDataSource, this);
        YAHOO.Bubbling.on("selectedItemsChanged", this._onSelectedItemsChanged, this);
        YAHOO.Bubbling.on("updateReportSourceColumns", this._onUpdateSourceColumns, this);
        YAHOO.Bubbling.on("unsubscribeBubbling", this.onUnsubscribe, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.SelectSourceEditor, Alfresco.component.Base, {
        dataSourceId: null,

        reportId: null,

        sourcesDataGrid: null,
        sourcesDataGridLabel: null,
        mainDataGridLabel: null,

        columnsDataGrid: null,
        columnsDataGridLabel: null,

        toolbarButtons: {
            "defaultActive": [],
            "activeSourceIsNew": []
        },

        groupActions: {},

        onUnsubscribe: function (layer, args) {
            YAHOO.Bubbling.unsubscribe("selectDataSource", this._onSelectDataSource, this);
            YAHOO.Bubbling.unsubscribe("updateReportSourceColumns", this._onUpdateSourceColumns, this);
            YAHOO.Bubbling.unsubscribe("selectedItemsChanged", this._onSelectedItemsChanged, this);
        },

        setDataSourceId: function (dataSourceId) {
            var context = this;
            if (dataSourceId && dataSourceId.length > 0) {
                this.dataSourceId = dataSourceId;
                Alfresco.util.Ajax.request(
                    {
                        method: "GET",
                        dataObj: {
                            dataSourceId: dataSourceId
                        },
                        url: Alfresco.constants.PROXY_URI + "lecm/reports-editor/getDataSourceColumns",
                        successCallback: {
                            fn: function (response) {
                                var oResults = eval("(" + response.serverResponse.responseText + ")");
                                if (oResults) {
                                    //список колонок храним в соответствующем гриде
                                    YAHOO.Bubbling.fire("updateActiveColumns", {
                                        columns: oResults,
                                        bubblingLabel: context.mainDataGridLabel
                                    });
                                    context.columnsDataGrid.activeSourceColumns = oResults;
                                }
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function (response) {
                                alert(response.json.message);
                                Alfresco.util.PopupManager.displayMessage({
                                    text: "Не удалось получить список столбцов для текущего набора данных"
                                });
                            }
                        },
                        execScripts: true,
                        requestContentType: "application/json",
                        responseContentType: "application/json"
                    });
            }
        },

        setReportId: function (reportId) {
            this.reportId = reportId;
        },

        setSourcesDataGridLabel: function (label) {
            this.sourcesDataGridLabel = label;
        },

        setMainDataGridLabel: function (label) {
            this.mainDataGridLabel = label;
        },

        setColumnsDataGridLabel: function (label) {
            this.columnsDataGridLabel = label;
        },

        onInitDataGrid: function (layer, args) {
            var datagrid = args[1].datagrid;
            if (datagrid.options.bubblingLabel == this.sourcesDataGridLabel) {
                this.sourcesDataGrid = datagrid;
            } else if (datagrid.options.bubblingLabel == this.columnsDataGridLabel) {
                this.columnsDataGrid = datagrid;
            }
        },

        onReady: function () {
            this._initButtons();
            Dom.setStyle(this.id + "-columns-toolbar-body", "visibility", "visible");
        },

        copyColumn: function (columnId, from, to, fireUpdateEvent) {
            var copyRefs = [];
            copyRefs.push(columnId);

            var copyTo = new Alfresco.util.NodeRef(to);

            Alfresco.util.Ajax.request(
                {
                    method: "POST",
                    dataObj: {
                        nodeRefs: copyRefs,
                        parentId: from
                    },
                    url: Alfresco.constants.PROXY_URI + "slingshot/doclib/action/copy-to/node/" + copyTo.uri,
                    successCallback: {
                        fn: function (response) {
                            if (response.json.overallSuccess) {
                                if (fireUpdateEvent) {
                                    Alfresco.util.PopupManager.displayMessage({
                                        text: "Столбец добавлен в набор"
                                    }, Dom.get("selectSourcePanel"));
                                    // обновляем список колонок
                                    YAHOO.Bubbling.fire("updateReportSourceColumns");
                                }
                            }
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function (response) {
                            alert(response.json.message);
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось скопировать шаблон"
                            });
                        }
                    },
                    execScripts: true,
                    requestContentType: "application/json",
                    responseContentType: "application/json"
                });
        },

        _onSelectedItemsChanged: function Toolbar_onSelectedItemsChanged(layer, args) {
            var obj = args[1];
            if (this.columnsDataGrid) {
                if (!obj || obj == this.columnsDataGrid.options.bubblingLabel) {
                    var items = this.columnsDataGrid.getSelectedItems();
                    for (var index in this.groupActions) {
                        if (this.groupActions.hasOwnProperty(index)) {
                            var action = this.groupActions[index];
                            action.set("disabled", (items.length === 0));
                        }
                    }
                }
            }
        },

        _onSelectDataSource: function (layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.dataSourceId !== null)) {
                this.setDataSourceId(obj.dataSourceId);
            }
        },

        _initButtons: function () {
            this.groupActions.selectColumnsBtn = Alfresco.util.createYUIButton(this, "selectColumnsBtn", this._onSelectColumns, {
                disabled: true
            }, this.id + "-columns-toolbar-selectColumnsBtn");
        },

        _onSelectColumns: function () {
            var selectedItems = this.columnsDataGrid.getSelectedItems();
            if (selectedItems.length != 0) {
                var items = YAHOO.lang.isArray(selectedItems) ? selectedItems : [selectedItems];
                for (var k = 0; k < items.length; k++) {
                    if (!this.existInSource(items[k].itemData["prop_lecm-rpeditor_dataColumnCode"].value)) {
                        this.copyColumn(items[k].nodeRef, this.columnsDataGrid.datagridMeta.nodeRef, this.dataSourceId, true);
                    }
                }
            } else {
                alert("Не выбран ни один элемент!");
            }
        },

        _onUpdateSourceColumns: function (layer, args) {
            this.setDataSourceId(this.dataSourceId);
        },

        existInSource: function (columnCode){
            for (var k = 0; k < this.columnsDataGrid.activeSourceColumns.length; k++) {
                if (this.columnsDataGrid.activeSourceColumns[k].code == columnCode){
                    return true;
                }
            }
            return false;
        }
    });
})();