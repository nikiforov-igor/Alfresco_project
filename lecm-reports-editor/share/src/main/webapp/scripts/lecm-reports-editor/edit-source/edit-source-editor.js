(function () {
    LogicECM.module.ReportsEditor.EditSourceEditor = function (htmlId) {
        LogicECM.module.ReportsEditor.EditSourceEditor.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.EditSourceEditor",
            htmlId,
            ["button", "container", "connection"]);

        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        /*YAHOO.Bubbling.on("copySourceToReport", this._copySourceToReport, this);
        YAHOO.Bubbling.on("refreshButtonState", this._onRefreshButtonState, this);
        YAHOO.Bubbling.on("selectDataSource", this._onSelectDataSource, this);
        YAHOO.Bubbling.on("onSearchSuccess", this._onSearchComplete, this);
        YAHOO.Bubbling.on("copyColumnToReportSource", this._onCopyColumn, this);
        YAHOO.Bubbling.on("updateReportSourceColumns", this._onUpdateSourceColumns, this);*/
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.EditSourceEditor, Alfresco.component.Base, {
        dataSourceId: null,
        isNewSource: false,

        reportId: null,

        columnsDataGrid: null,

        toolbarButtons: {
            "defaultActive": []
        },


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
            if (datagrid.options.bubblingLabel == "sourceColumns") {
                this.columnsDataGrid = datagrid;
            }
        },

        onReady: function () {
            this._initButtons();

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
                                /*//блокируем кнопки - Сохранить как - данный набор уже и так сохранен
                                YAHOO.Bubbling.fire("refreshButtonState", {
                                    bubblingLabel: "sourceColumns",
                                    disabledButtons: ["activeSourceIsNew"]
                                });*/
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

        _initButtons: function () {
            this.toolbarButtons['defaultActive'].push(
                Alfresco.util.createYUIButton(this, "newColumnButton", this._onNewColumn, {value: "create", disabled: !this.dataSourceId}, this.id + "-columns-toolbar-newColumnButton")
            );

            this.toolbarButtons['defaultActive'].push(
                Alfresco.util.createYUIButton(this, "saveAsButton", this._onCopySource, {value: "create", disabled: !this.isNewSource || !this.dataSourceId}, this.id + "-columns-toolbar-saveAsButton")
            );

            this.toolbarButtons['defaultActive'].push(
                Alfresco.util.createYUIButton(this, "prevPageButton", this._onPrevPage, {}, this.id + "-columns-toolbar-prevPageButton")
            );

            this.toolbarButtons['defaultActive'].push(
                Alfresco.util.createYUIButton(this, "nextPageButton", this._onNextPage, {}, this.id + "-columns-toolbar-nextPageButton")
            );
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
                Alfresco.constants.URL_PAGECONTEXT + "reports-editor-template-edit?reportId=" + context.reportId;
        },

        _onPrevPage: function () {
            var context = this;
            window.location.href = window.location.protocol + "//" + window.location.host +
                Alfresco.constants.URL_PAGECONTEXT + "reports-editor-source-add?reportId=" + context.reportId;
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
        }
    });
})();