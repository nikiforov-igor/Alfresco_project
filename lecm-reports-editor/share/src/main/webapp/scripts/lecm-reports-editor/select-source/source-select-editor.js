(function () {
    LogicECM.module.ReportsEditor.SelectSourceEditor = function (htmlId) {
        LogicECM.module.ReportsEditor.SelectSourceEditor.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.SelectSourceEditor",
            htmlId,
            ["button", "container", "connection"]);

        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("copySourceToReport", this._copySourceToReport, this);
        YAHOO.Bubbling.on("selectDataSource", this._onSelectDataSource, this);
        YAHOO.Bubbling.on("copyColumnToReportSource", this._onCopyColumn, this);
        YAHOO.Bubbling.on("selectedItemsChanged", this._onSelectedItemsChanged, this);
        YAHOO.Bubbling.on("updateReportSourceColumns", this._onUpdateSourceColumns, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.SelectSourceEditor, Alfresco.component.Base, {
        dataSourceId: null,

        reportId: null,

        sourcesDataGrid: null,
        sourcesDataGridLabel: null,

        columnsDataGrid: null,
        columnsDataGridLabel: null,

        toolbarButtons: {
            "defaultActive": [],
            "activeSourceIsNew": []
        },

        groupActions: {},

        setDataSourceId: function (dataSourceId) {
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
                                    this.columnsDataGrid.activeSourceColumns = oResults;
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

        copySource: function (sourceId, from, to, fromRepo) {
            var copyRefs = [];
            copyRefs.push(sourceId);

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
                                Alfresco.util.PopupManager.displayMessage({
                                    text: "Набор скопирован"
                                });
                                //блокируем кнопки - Сохранить как - данный набор уже и так сохранен
                                YAHOO.Bubbling.fire("refreshButtonState", {
                                    bubblingLabel: this.columnsDataGridLabel,
                                    disabledButtons: ["activeSourceIsNew"]
                                });
                                //if (fireCreateEvent) {
                                    // добаляем запись в таблицу и обновляем данные
                                    YAHOO.Bubbling.fire("dataItemCreated",
                                        {
                                            nodeRef: response.json.results[0].nodeRef,
                                            oldNodeRef: fromRepo ? this.dataSourceId : null,
                                            copiedRef: sourceId,
                                            bubblingLabel: this.sourcesDataGrid.options.bubblingLabel
                                        });
                                //}
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

        _copySourceToReport: function (layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.dataSourceId !== null) && (obj.bubblingLabel == this.sourcesDataGridLabel)) {
                if (obj.dataSourceId) {
                    Alfresco.util.Ajax.request(
                        {
                            method: "GET",
                            dataObj: {
                                dataSourceId: obj.dataSourceId
                            },
                            url: Alfresco.constants.PROXY_URI + "lecm/reports-editor/getDataSourceColumns",
                            successCallback: {
                                fn: function (response) {
                                    var oResults = eval("(" + response.serverResponse.responseText + ")");
                                    if (oResults) {
                                        for (var k = 0; k < oResults.length; k++) {
                                            if (!this.existInSource(oResults[k].code)) {
                                                this.copyColumn(oResults[k].nodeRef, obj.dataSourceId, this.dataSourceId, true);
                                            }
                                            if (k == oResults.length - 1) {
                                                Alfresco.util.PopupManager.displayMessage({
                                                        text: "Набор скопирован"
                                                    }, Dom.get("selectSourcePanel")
                                                );
                                            }
                                        }
                                    }
                                },
                                scope: this
                            },
                            failureCallback: {
                                fn: function (response) {
                                    alert(response.json.message);
                                    Alfresco.util.PopupManager.displayMessage({
                                        text: "Не удалось получить список столбцов для набора данных"
                                    }, Dom.get("selectSourcePanel"));
                                }
                            },
                            execScripts: true,
                            requestContentType: "application/json",
                            responseContentType: "application/json"
                        });
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

        _onCopyColumn: function (layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.columnId !== null)) {
                if (this.dataSourceId) {
                    this.copyColumn(obj.columnId, obj.sourceId, this.dataSourceId, true);
                } else {
                    Alfresco.util.PopupManager.displayMessage({
                        text: "Нет активного набора!"
                    });
                }
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