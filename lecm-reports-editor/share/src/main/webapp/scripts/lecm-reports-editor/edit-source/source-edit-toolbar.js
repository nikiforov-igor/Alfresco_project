(function () {

    var Dom = YAHOO.util.Dom;

    LogicECM.module.ReportsEditor.EditSourceToolbar = function (htmlId) {
        LogicECM.module.ReportsEditor.EditSourceToolbar.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.EditSourceToolbar",
            htmlId,
            ["button", "container", "connection"]);

        this.selectSourcePanel = null;

        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("updateReportSourceColumns", this._onUpdateSourceColumns, this);
        YAHOO.Bubbling.on("copyColumnToReportSource", this._onCopyColumn, this);
        YAHOO.Bubbling.on("selectedItemsChanged", this._onSelectedItemsChanged, this);
        YAHOO.Bubbling.on("onSearchSuccess", this._toolbarButtonActivate, this);
        YAHOO.Bubbling.on("dataItemsDeleted", this._toolbarButtonActivate, this);
        YAHOO.Bubbling.on("unsubscribeBubbling", this.onUnsubscribe, this);
        YAHOO.Bubbling.on("copySourceToReport", this._copySourceToReport, this);
        YAHOO.Bubbling.on("updateActiveColumns", this.onUpdateActiveColumns, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.EditSourceToolbar, Alfresco.component.Base, {

        dataSourceId: null,

        reportId: null,

        columnsDataGrid: null,

        toolbarButtons: {},

        selectSourcePanel : null,

        groupActions: {},

        isCopy: false,

        formMode: "create",

        itemKind: "type",

        items: [],

        formId: "",

        doubleClickLock: false,

        bubblingLabel: null,

        setBubblingLabel: function (label) {
            this.bubblingLabel = label;
        },

        setDataSourceId: function (dataSourceId) {
            if (dataSourceId && dataSourceId.length > 0) {
                this.dataSourceId = dataSourceId;
            }
        },

        setReportId: function (reportId) {
            this.reportId = reportId;
        },

        onUnsubscribe: function (layer, args) {
            YAHOO.Bubbling.unsubscribe("initDatagrid", this.onInitDataGrid, this);
            YAHOO.Bubbling.unsubscribe("updateReportSourceColumns", this._onUpdateSourceColumns, this);
            YAHOO.Bubbling.unsubscribe("selectedItemsChanged", this._onSelectedItemsChanged, this);
            YAHOO.Bubbling.unsubscribe("onSearchSuccess", this._toolbarButtonActivate, this);
            YAHOO.Bubbling.unsubscribe("dataItemsDeleted", this._toolbarButtonActivate, this);
        },

        onUpdateActiveColumns: function (layer, args){
            var obj = args[1];
            if ((obj !== null) && (obj.columns !== null) && (obj.bubblingLabel == this.bubblingLabel)) {
                this.columnsDataGrid.activeSourceColumns = obj.columns;
            }
        },

        onInitDataGrid: function (layer, args) {
            var datagrid = args[1].datagrid;
            if (datagrid.options.bubblingLabel == this.bubblingLabel) {
                this.columnsDataGrid = datagrid;
                this.setDataSourceId(this.columnsDataGrid.datagridMeta.nodeRef);

                this.createSelectDialog();
                this._initButtons();
                Dom.setStyle(this.id + "-body", "visibility", "visible");
            }
        },

        createSelectDialog: function() {
            this.selectSourcePanel = Alfresco.util.createYUIPanel("selectSourcePanel",
                {
                    width: "900px"
                });
            YAHOO.Bubbling.on("hidePanel", this._hideSelectDialog);
            this.widgets.closeButton = Alfresco.util.createYUIButton(this, null, this._onClose, {}, Dom.get("selectSourcePanel-close-button"));
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

        _initButtons: function () {
            this.toolbarButtons.newColumnButton =
                Alfresco.util.createYUIButton(this, "newColumnButton", this._onNewColumn, {value: "create", disabled: !this.dataSourceId}, this.id + "-newColumnButton");

            this.toolbarButtons.saveAsButton =
                Alfresco.util.createYUIButton(this, "saveAsButton", this._onCopySource, {value: "create", disabled: !this.dataSourceId}, this.id + "-saveAsButton");

            this.toolbarButtons.selectSource =
                Alfresco.util.createYUIButton(this, "selectSource", this._onSelectSource, {disabled: !this.dataSourceId}, this.id + "-selectSource");

            this.groupActions.deleteColumnsBtn = Alfresco.util.createYUIButton(this, "deleteColumnsBtn", this._onDeleteColumns, {disabled: true}, this.id + "-deleteColumnsBtn");
        },

        _onDeleteColumns: function() {
            if (this.columnsDataGrid) {
                var fn = "onActionDelete";
                if (fn && (typeof this.columnsDataGrid[fn] == "function")) {
                    this.columnsDataGrid[fn].call(this.columnsDataGrid, this.columnsDataGrid.getSelectedItems(), null, {fullDelete: true, trash: false}, function () {YAHOO.Bubbling.fire("updateReportSourceColumns");});
                }
            }
        },

        _hideSelectDialog: function(layer, args){
            var mayHide = false;
            if (this.selectSourcePanel != null) {
                if (args == undefined || args == null) {
                    mayHide = true;
                } else if (args[1] && args[1].panel && args[1].panel.id == this.selectSourcePanel.id){
                    mayHide = true
                }
                if (mayHide){
                    if (typeof this.selectSourcePanel.hide == 'function') {
                        this.selectSourcePanel.hide();
                    }
                    Dom.setStyle("selectSourcePanel", "display", "none");
                    var formDiv = Dom.get("selectSourcePanel-form");
                    formDiv.innerHTML = "";
                }
            }
        },

        _viewSelectDialog: function () {
            if (this.selectSourcePanel) {
                Dom.setStyle("selectSourcePanel", "display", "block");
                this.selectSourcePanel.show();
            } else {
                this.createSelectDialog();
                this._viewSelectDialog();
            }
        },

        _onSelectSource: function () {
            Alfresco.util.Ajax.request(
                {
                    method: "GET",
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/reports-editor/source-select?reportId=" + this.reportId + "&gridLabel=" + this.bubblingLabel,
                    successCallback: {
                        fn: function (response) {
                            var formDiv = Dom.get("selectSourcePanel-form");
                            formDiv.innerHTML = "";
                            //YAHOO.Bubbling.fire("unsubscribeBubbling");
                            formDiv.innerHTML = response.serverResponse.responseText;
                            this._viewSelectDialog();
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function () {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось получить список шаблонов"
                            });
                        }
                    },
                    execScripts: true
                });
        },

        _onNewColumn: function () {
            this.isCopy = false;
            if (this.dataSourceId) {
                var formId =
                    (LogicECM.module.ReportsEditor.REPORT_SETTINGS && LogicECM.module.ReportsEditor.REPORT_SETTINGS.isSQLReport == "true") ? "sql-provider-column" : "";
                this._showCreateForm({
                    itemType: "lecm-rpeditor:reportDataColumn",
                    nodeRef: this.dataSourceId,
                    itemKind: "type",
                    formMode: "create",
                    formId: formId
                });
            } else {
                Alfresco.util.PopupManager.displayMessage({
                    text: "Нет активного набора!"
                });
            }
        },

        _onClose: function () {
            this._hideSelectDialog();
        },

        _onCopySource: function () {
            if (this.dataSourceId) {
                this.isCopy = true;
                this._showCreateForm({
                    formMode: "edit",
                    itemKind: "node",
                    nodeRef: LogicECM.module.ReportsEditor.SETTINGS.sourcesContainer,
                    itemType: this.dataSourceId
                })
            } else {
                Alfresco.util.PopupManager.displayMessage({
                    text: "Нет активного набора!"
                });
            }
        },

        _showCreateForm: function (meta) {
            if (this.doubleClickLock) return;
            this.doubleClickLock = true;
            var doBeforeDialogShow = function (p_form, p_dialog) {
                var createMsg = (this.isCopy) ? this.msg("label.save-as-source.title") : this.msg("label.create-column.title");
                Alfresco.util.populateHTML(
                    [ p_dialog.id + "-form-container_h", createMsg ]
                );

                Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
                if (this.isCopy) {
                    this.items = p_dialog.form.validations;
                    this._setInputValue('-createColumnDetails_prop_cm_name',"");
                    this._setInputValue('-createColumnDetails_prop_lecm-rpeditor_dataSourceCode',"");
                }
                this.doubleClickLock = false;
            };
            var context = this;
            var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
                {
                    itemKind: meta.itemKind,
                    itemId: meta.itemType,
                    destination: meta.nodeRef,
                    mode: meta.formMode,
                    submitType: "json",
                    formId: meta.formId
                });

            var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createColumnDetails");
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
                    doBeforeFormSubmit: {
                        fn: function InstantAbsence_doBeforeSubmit() {
                            if (this.isCopy) {
                                var form = Dom.get(this.id + "-createColumnDetails-form");
                                form.setAttribute("action", Alfresco.constants.PROXY_URI_RELATIVE + "lecm/reports-editor/copy-report-source");
                                this._formAddElemet(form, "input", "copyToFile", LogicECM.module.ReportsEditor.SETTINGS.sourcesContainer);
                                this._formAddElemet(form, "input", "copyRef", this.dataSourceId);
                            }

                        },
                        scope: this
                    },
                    onSuccess: {
                        fn: function DataGrid_onActionCreate_success(response) {
                            if (this.isCopy) {
                                var message = (response.json.success) ? this.msg("message.copy.success")  : this.msg("message.copy.failure");
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: message
                                    });
                            } else {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.success")
                                    });

                                YAHOO.Bubbling.fire("dataItemCreated",
                                    {
                                        nodeRef: response.json.persistedObject,
                                        bubblingLabel: context.bubblingLabel
                                    });
                                this.toolbarButtons.saveAsButton.set("disabled",false);
                            }
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

        _formAddElemet: function(form, tag, nameId, value) {
            input = document.createElement(tag);
            input.setAttribute("id", this.id + "-createDetails-form-" + nameId);
            input.setAttribute("type", "hidden");
            input.setAttribute("name", nameId);
            input.setAttribute("value", value);
            form.appendChild(input);
        },

        _setInputValue: function(name, value) {
            var htmlItem = Dom.get(this.id + name);
            if (htmlItem) {
                htmlItem.setAttribute("value", value);
            }
        },

        _onUpdateSourceColumns: function () {
            YAHOO.Bubbling.fire("activeGridChanged", {
                datagridMeta: {
                    itemType: "lecm-rpeditor:reportDataColumn",
                    nodeRef: this.dataSourceId,
                    useChildQuery:true,
                    actionsConfig: {
                        fullDelete: true,
                        trash: false
                    },
                    sort: "lecm-rpeditor:dataColumnCode|true"
                },
                bubblingLabel: this.bubblingLabel
            });
        },

        _toolbarButtonActivate: function (layer, args) {
            var obj = args[1];
            if (this.columnsDataGrid) {
                if (!obj || obj == this.columnsDataGrid.options.bubblingLabel) {
                    this.toolbarButtons.saveAsButton.set("disabled", (this.columnsDataGrid.modules.dataGrid.totalRecords === 0));
                }
            }
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
            if ((obj !== null) && (obj.dataSourceId !== null) && (obj.bubblingLabel == this.bubblingLabel)) {
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

        _onCopyColumn: function (layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.columnId !== null) && obj.bubblingLabel == this.bubblingLabel) {
                if (this.dataSourceId) {
                    this.copyColumn(obj.columnId, obj.sourceId, this.dataSourceId, true);
                } else {
                    Alfresco.util.PopupManager.displayMessage({
                        text: "Нет активного набора!"
                    });
                }
            }
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