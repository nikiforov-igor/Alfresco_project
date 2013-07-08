(function () {

    var Dom = YAHOO.util.Dom;

    LogicECM.module.ReportsEditor.EditSourceEditor = function (htmlId) {
        LogicECM.module.ReportsEditor.EditSourceEditor.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.EditSourceEditor",
            htmlId,
            ["button", "container", "connection"]);

        this.selectSourcePanel = null;

        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        YAHOO.Bubbling.on("updateReportSourceColumns", this._onUpdateSourceColumns, this);
        YAHOO.Bubbling.on("selectedItemsChanged", this._onSelectedItemsChanged, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.EditSourceEditor, Alfresco.component.Base, {

        dataSourceId: null,

        isNewSource: false,

        reportId: null,

        columnsDataGrid: null,

        toolbarButtons: {},

        selectSourcePanel : null,

        groupActions: {},

        markAsNewSource: function (isNew) {
            this.isNewSource = isNew;
        },

        setDataSourceId: function (dataSourceId) {
            if (dataSourceId && dataSourceId.length > 0) {
                this.dataSourceId = dataSourceId;
            }
        },

        setReportId: function (reportId) {
            this.reportId = reportId;
        },

        onInitDataGrid: function (layer, args) {
            var datagrid = args[1].datagrid;
            if (datagrid.options.bubblingLabel == "source-columns") {
                this.columnsDataGrid = datagrid;
            }
        },

        onReady: function () {
            this.createSelectDialog();
            this._initButtons();
            Dom.setStyle(this.id + "-columns-toolbar-body", "visibility", "visible");
        },

        createSelectDialog: function() {
            this.selectSourcePanel = Alfresco.util.createYUIPanel("selectSourcePanel",
                {
                    width: "800px"
                });
            YAHOO.Bubbling.on("hidePanel", this._hideSelectDialog);
            this.widgets.closeButton = Alfresco.util.createYUIButton(this, "searchBlock-search-button", this._onClose, {}, Dom.get("selectSourcePanel-close-button"));
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
                Alfresco.util.createYUIButton(this, "newColumnButton", this._onNewColumn, {value: "create", disabled: !this.dataSourceId}, this.id + "-columns-toolbar-newColumnButton");

            this.toolbarButtons.saveAsButton =
                Alfresco.util.createYUIButton(this, "saveAsButton", this._onCopySource, {value: "create", disabled: !this.isNewSource || !this.dataSourceId}, this.id + "-columns-toolbar-saveAsButton");

            this.toolbarButtons.selectSource =
                Alfresco.util.createYUIButton(this, "selectSource", this._onSelectSource, {disabled: !this.dataSourceId}, this.id + "-columns-toolbar-selectSource");

            this.groupActions.deleteColumnsBtn = Alfresco.util.createYUIButton(this, "deleteColumnsBtn", this._onDeleteColumns, {disabled: true}, this.id + "-columns-toolbar-deleteColumnsBtn");
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
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/reports-editor/source-select?reportId=" + this.reportId,
                    successCallback: {
                        fn: function (response) {
                            var formDiv = Dom.get("selectSourcePanel-form");
                            formDiv.innerHTML = response.serverResponse.responseText;
                            this._viewSelectDialog();
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
                    execScripts: true
                });
        },

        _onNewColumn: function () {
            if (this.dataSourceId) {
                this._showCreateForm({
                    itemType: "lecm-rpeditor:reportDataColumn",
                    nodeRef: this.dataSourceId
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
                this.copySource(this.dataSourceId, this.reportId, LogicECM.module.ReportsEditor.SETTINGS.sourcesContainer, false);
            } else {
                Alfresco.util.PopupManager.displayMessage({
                    text: "Нет активного набора!"
                });
            }
        },

        _showCreateForm: function (meta) {
            var doBeforeDialogShow = function (p_form, p_dialog) {
                var createMsg = this.msg("label.create-column.title");
                Alfresco.util.populateHTML(
                    [ p_dialog.id + "-form-container_h", createMsg ]
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

            var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createColumnDetails");
            createDetails.setOptions(
                {
                    width: "45em",
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

                            YAHOO.Bubbling.fire("dataItemCreated",
                                {
                                    nodeRef: response.json.persistedObject,
                                    bubblingLabel: "source-columns"
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

        _onUpdateSourceColumns: function () {
            YAHOO.Bubbling.fire("activeGridChanged", {
                datagridMeta: {
                    itemType: "lecm-rpeditor:reportDataColumn",
                    nodeRef: this.dataSourceId,
                    actionsConfig: {
                        fullDelete: true,
                        trash: false
                    },
                    sort: "lecm-rpeditor:dataColumnCode|true"
                },
                bubblingLabel: "source-columns"
            });
        },

        _onSelectedItemsChanged: function Toolbar_onSelectedItemsChanged(layer, args) {
            var obj = args[1];
            if (this.columnsDataGrid) {
                if (!obj.bubblingLabel || obj.bubblingLabel == this.columnsDataGrid.options.bubblingLabel) {
                    var items = this.columnsDataGrid.getSelectedItems();
                    for (var index in this.groupActions) {
                        if (this.groupActions.hasOwnProperty(index)) {
                            var action = this.groupActions[index];
                            action.set("disabled", (items.length === 0));
                        }
                    }
                }
            }
        }
    });
})();