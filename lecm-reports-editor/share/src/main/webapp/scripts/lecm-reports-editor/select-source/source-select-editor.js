(function () {
    LogicECM.module.ReportsEditor.SelectSourceEditor = function (htmlId) {
        LogicECM.module.ReportsEditor.SelectSourceEditor.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.SelectSourceEditor",
            htmlId,
            ["button", "container", "connection"]);

        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("copySourceToReport", this._copySourceToReport, this);
        YAHOO.Bubbling.on("refreshButtonState", this._onRefreshButtonState, this);
        YAHOO.Bubbling.on("selectDataSource", this._onSelectDataSource, this);
        YAHOO.Bubbling.on("onSearchSuccess", this._onSearchComplete, this);
        YAHOO.Bubbling.on("copyColumnToReportSource", this._onCopyColumn, this);
        YAHOO.Bubbling.on("updateReportSourceColumns", this._onUpdateSourceColumns, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.SelectSourceEditor, Alfresco.component.Base, {
        dataSourceId: null,
        isNewSource: false,

        reportId: null,

        sourcesDataGrid: null,
        columnsDataGrid: null,

        toolbarButtons: {
            "defaultActive": [],
            "activeSourceIsNew": []
        },


        markAsNewSource: function (isNew) {
            this.isNewSource = isNew;
        },

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
                            fn: function () {
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

        onInitDataGrid: function (layer, args) {
            var datagrid = args[1].datagrid;
            if (datagrid.options.bubblingLabel == "sourcesList") {
                this.sourcesDataGrid = datagrid;
            } else if (datagrid.options.bubblingLabel == "sourceColumns") {
                this.columnsDataGrid = datagrid;
            }
        },

        onReady: function () {
            this._initButtons();

            Dom.setStyle(this.id + "-sources-toolbar-body", "visibility", "visible");
            Dom.setStyle(this.id + "-columns-toolbar-body", "visibility", "visible");
        },

        copySource: function (sourceId, from, to, fireCreateEvent) {
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
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Набор скопирован"
                            });
                            if (response.json.overallSuccess) {
                                //блокируем кнопки - Сохранить как - данный набор уже и так сохранен
                                YAHOO.Bubbling.fire("refreshButtonState", {
                                    bubblingLabel: "sourceColumns",
                                    disabledButtons: ["activeSourceIsNew"]
                                });
                                if (fireCreateEvent) {
                                    // добаляем запись в таблицу и обновляем данные
                                    YAHOO.Bubbling.fire("dataItemCreated",
                                        {
                                            nodeRef: response.json.results[0].nodeRef,
                                            oldNodeRef: this.dataSourceId,
                                            copiedRef: sourceId,
                                            bubblingLabel: "sourcesList"
                                        });
                                }
                            }
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function () {
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
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Столбец добавлен в набор"
                            });
                            if (response.json.overallSuccess) {
                                if (fireUpdateEvent) {
                                    // обновляем список колонок
                                    YAHOO.Bubbling.fire("updateReportSourceColumns");
                                }
                            }
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function () {
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

        _onSearchComplete: function (layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.bubblingLabel !== null)) {
                if (obj.bubblingLabel == "sourcesList" && this.dataSourceId) {
                    var recordFound = this.sourcesDataGrid._findRecordByParameter(this.dataSourceId, "nodeRef");
                    if (recordFound !== null) {
                        this.sourcesDataGrid.widgets.dataTable.unselectAllRows();
                        this.sourcesDataGrid.widgets.dataTable.selectRow(recordFound);
                        recordFound.getData().itemData.selected = true;

                        //помечаем запись. откуда был скопирован набор как выбранную
                        var copiedFrom = recordFound.getData().itemData["prop_cm_name"].value;
                        var copiedFromRecords = this._findRecordsByParameter(this.sourcesDataGrid, "prop_cm_name", copiedFrom);
                        if (copiedFromRecords) {
                            for (var i = 0; i < copiedFromRecords.length; i++) {
                                copiedFromRecords[i].getData().itemData.selected = true;
                            }

                        }
                        //this.sourcesDataGrid.widgets.dataTable.fireEvent("renderEvent", {type: "renderEvent"});
                        /*YAHOO.Bubbling.fire("activeGridChanged",
                            {
                                datagridMeta: {
                                    itemType: "lecm-rpeditor:reportDataColumn",
                                    nodeRef: recordFound.getData().nodeRef
                                },
                                bubblingLabel: "sourceColumns"
                            });*/
                    }
                }
            }
        },

        _copySourceToReport: function (layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.dataSourceId !== null)) {
                if (obj.dataSourceId != this.dataSourceId) {
                    this.copySource(obj.dataSourceId, LogicECM.module.ReportsEditor.SETTINGS.sourcesContainer, this.reportId, true);
                } else {
                    Alfresco.util.PopupManager.displayMessage({
                        text: "Данный шаблон уже выбран!"
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
            this.toolbarButtons['defaultActive'].push(
                Alfresco.util.createYUIButton(this, "newSourceButton", this._onNewSource, {value: "create"}, this.id + "-sources-toolbar-newSourceButton")
            );

            this.toolbarButtons['activeSourceIsNew'].push(
                Alfresco.util.createYUIButton(this, "saveButton", this._onSaveSource, {value: "create", disabled: !this.isNewSource || !this.dataSourceId}, this.id + "-columns-toolbar-saveButton")
            );

            this.toolbarButtons['activeSourceIsNew'].push(
                Alfresco.util.createYUIButton(this, "saveAsButton", this._onCopySource, {value: "create", disabled: !this.isNewSource || !this.dataSourceId}, this.id + "-columns-toolbar-saveAsButton")
            );

            this.toolbarButtons['defaultActive'].push(
                Alfresco.util.createYUIButton(this, "prevPageButton", this._onPrevPage, {}, this.id + "-columns-toolbar-prevPageButton")
            );

            this.toolbarButtons['defaultActive'].push(
                Alfresco.util.createYUIButton(this, "nextPageButton", this._onNextPage, {}, this.id + "-columns-toolbar-nextPageButton")
            );
        },

        _onNewSource: function () {
            this._showCreateForm({itemType: "lecm-rpeditor:reportDataSource", nodeRef: this.reportId});
        },

        _onSaveSource: function () {
            alert("Не реализовано!");
        },

        _onCopySource: function () {
            if (this.dataSourceId) {
                this.copySource(this.dataSourceId, this.reportId, LogicECM.module.ReportsEditor.SETTINGS.sourcesContainer, false);
            } else {
                Alfresco.util.PopupManager.displayMessage({
                    text: "Нет активного набора!"
                });
            }
        },

        _onNextPage: function () {
            var context = this;
            window.location.href = window.location.protocol + "//" + window.location.host +
                Alfresco.constants.URL_PAGECONTEXT + "reports-editor-source-edit?reportId=" + context.reportId;
        },

        _onPrevPage: function () {
            var context = this;
            window.location.href = window.location.protocol + "//" + window.location.host +
                Alfresco.constants.URL_PAGECONTEXT + "reports-editor?reportId=" + context.reportId;
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

        _showCreateForm: function (meta) {
            var doBeforeDialogShow = function (p_form, p_dialog) {
                var defaultMsg = this.msg("label.create-source.title");
                Alfresco.util.populateHTML(
                    [ p_dialog.id + "-form-container_h", defaultMsg ]
                );

                Dom.addClass(p_dialog.id + "-form", "metadata-form-edit");
            };

            var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
                {
                    itemKind: "type",
                    itemId: meta.itemType,
                    destination: meta.nodeRef,
                    mode: "create",
                    submitType: "json"
                });

            var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
            createDetails.setOptions(
                {
                    width: "50em",
                    templateUrl: templateUrl,
                    actionUrl: null,
                    destroyOnHide: true,
                    doBeforeDialogShow: {
                        fn: doBeforeDialogShow,
                        scope: this
                    },
                    onSuccess: {
                        fn: function DataGrid_onActionCreate_success(response) {
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text: this.msg("message.save.success")
                                });

                            // рисуем кнопки
                            YAHOO.Bubbling.fire("refreshButtonState", {
                                bubblingLabel: "sourceColumns",
                                enabledButtons: ["activeSourceIsNew"]
                            });

                            YAHOO.Bubbling.fire("dataItemCreated",
                                {
                                    nodeRef: response.json.persistedObject,
                                    oldNodeRef: this.dataSourceId,
                                    bubblingLabel: "sourcesList"
                                });
                        },
                        scope: this
                    },
                    onFailure: {
                        fn: function DataGrid_onActionCreate_failure(response) {
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text: this.msg("message.save.failure")
                                });
                        },
                        scope: this
                    }
                }).show();
        },

        _onRefreshButtonState: function Tree_onRefreshButtonsState(layer, args) {
            var obj = args[1];
            var flag, buttons, button;
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
                                        button.set("disabled", false);
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
        },

        _findToolbarButton: function (id) {
            var button, buttons;
            for (var index in this.toolbarButtons) {
                if (this.toolbarButtons.hasOwnProperty(index)) {
                    buttons = this.toolbarButtons[index];
                    for (var btnIndx in buttons) {
                        if (buttons.hasOwnProperty(btnIndx)) {
                            button = buttons[btnIndx];
                            if (button != null) {
                                if (button.get("id").indexOf(id)) {
                                    return button;
                                }
                            }
                        }
                    }
                }
            }
            return null;
        },

        _findRecordsByParameter: function (grid, parameter, value) {
            var records = [];
            var recordSet = grid.widgets.dataTable.getRecordSet();
            var index = 0;
            if (grid.widgets.paginator) {
                index = ((grid.widgets.paginator.getCurrentPage() - 1) * grid.options.pageSize);
            }
            for (var i = index, j = recordSet.getLength(); i < j; i++) {
                if (recordSet.getRecord(i).getData().itemData[parameter].value == value) {
                    records.push(recordSet.getRecord(i));
                }
            }
            return records;
        }
    });
})();