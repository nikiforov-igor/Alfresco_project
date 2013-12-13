<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<div class="yui-t1" id="report-editor-sourcesList">
<div id="yui-main">
    <div class="yui-b" id="alf-content-2">
    <#assign columns_id = "columnsList-toolbar">
        <div id="${columns_id}">
            <script type="text/javascript">//<![CDATA[
            function init() {
                new LogicECM.module.ReportsEditor.Toolbar("${columns_id}").setMessages(${messages}).setOptions({
                    bubblingLabel: "sourceColumns",
                    newRowDialogTitle: "label.create-source-column.title",
                    newRowButtonType:"activeOnParentTableClick"
                });
            }
            YAHOO.util.Event.onDOMReady(init);
            //]]></script>
            <@comp.baseToolbar columns_id true false false>
            <div class="new-row">
                    <span id="${columns_id}-newElementButton" class="yui-button yui-push-button">
                       <span class="first-child">
                          <button type="button"
                                  title="${msg("label.new-source-column.btn")}">${msg("label.new-source-column.btn")}</button>
                       </span>
                    </span>
            </div>
        </@comp.baseToolbar>
        </div>
    <#assign columns_id = "columnsList-grid">
        <div id="${columns_id}">
            <!-- DataGrid -->
            <div id="yui-main-2">
                <div id="${columns_id}-alf-content-2">
                    <@grid.datagrid id=columns_id showViewForm=false showArchiveCheckBox=false>
                    <script type="text/javascript">//<![CDATA[
                    LogicECM.module.ReportsEditor.ColumnsGrid = function (containerId) {
                        return LogicECM.module.ReportsEditor.ColumnsGrid.superclass.constructor.call(this, containerId);
                    };

                    /**
                     * Extend from LogicECM.module.Base.DataGrid
                     */
                    YAHOO.lang.extend(LogicECM.module.ReportsEditor.ColumnsGrid, LogicECM.module.Base.DataGrid);

                    function createColumnsDatagrid() {
                        var datagrid = new LogicECM.module.ReportsEditor.ColumnsGrid('columnsList-grid').setOptions(
                                {
                                    usePagination: true,
                                    useDynamicPagination: false,
                                    showExtendSearchBlock: false,
                                    showActionColumn: true,
                                    overrideSortingWith: false,
                                    actions: [
                                        {
                                            type: "datagrid-action-link-sourceColumns",
                                            id: "onActionEdit",
                                            permission: "edit",
                                            label: "${msg("actions.edit")}"
                                        },
                                        {
                                            type: "datagrid-action-link-sourceColumns",
                                            id: "onActionDelete",
                                            permission: "delete",
                                            label: "${msg("actions.delete-row")}",
                                            evaluator: function (rowData) {
                                                var itemData = rowData.itemData;
                                                return this.isActiveItem(itemData);
                                            }
                                        }
                                    ],
                                    bubblingLabel: "sourceColumns",
                                    showCheckboxColumn: false
                                }).setMessages(${messages});

                        YAHOO.util.Event.onContentReady('columnsList-grid', function () {
                            YAHOO.Bubbling.fire("activeGridChanged", {
                                datagridMeta: {
                                    itemType: "lecm-rpeditor:reportDataColumn",
                                    nodeRef: "NOT_LOAD",
                                    actionsConfig: {
                                        fullDelete: true,
                                        trash:false
                                    },
                                    sort: "lecm-rpeditor:dataColumnCode|true"
                                },
                                bubblingLabel: "sourceColumns"
                            });
                        });
                    }

                    function initColumnsDatagrid() {
                        createColumnsDatagrid();

                        var resizer = new LogicECM.module.Base.Resizer('ReportsEditorResizer');
                        resizer.setOptions({
                            initialWidth: 350,
                            divLeft: "alf-filters-2",
                            divRight: "alf-content-2"
                        });
                    }

                    YAHOO.util.Event.onDOMReady(initColumnsDatagrid);
                    //]]></script>
                </@grid.datagrid>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="alf-filters-2">
    <#assign sources_id = "sourcesList-toolbar">
    <div id="${sources_id}">
    <!-- Toolbar-->
    <script type="text/javascript">//<![CDATA[
    function init() {
        new LogicECM.module.ReportsEditor.Toolbar("${sources_id}").setMessages(${messages}).setOptions({
            bubblingLabel: "sourcesList",
            newRowDialogTitle: "label.create-source.title",
            newRowButtonType:"defaultActive"
        });
    }
    YAHOO.util.Event.onDOMReady(init);
    //]]></script>
    <@comp.baseToolbar sources_id true false false>
    <div class="new-row">
                <span id="${sources_id}-newElementButton" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button"
                                title="${msg("label.new-source.btn")}">${msg("label.new-source.btn")}</button>
                    </span>
                </span>
    </div>
</@comp.baseToolbar>
</div>
    <#assign sources_id = "sourcesList-grid">
    <div id="${sources_id}">
<!-- DataGrid -->
        <div id="yui-main-3">
            <div id="${sources_id}-alf-content2">
                <@grid.datagrid id=sources_id showViewForm=false showArchiveCheckBox=false>
        <script type="text/javascript">//<![CDATA[
            LogicECM.module.ReportsEditor.SourcesGrid = function (containerId) {
                return LogicECM.module.ReportsEditor.SourcesGrid.superclass.constructor.call(this, containerId);
            };

            YAHOO.lang.extend(LogicECM.module.ReportsEditor.SourcesGrid, LogicECM.module.Base.DataGrid);

            /**
             * Augment prototype with main class implementation, ensuring overwrite is enabled
             */
            YAHOO.lang.augmentObject(LogicECM.module.ReportsEditor.SourcesGrid.prototype, {
                _setupDataTable: function (columnDefinitions, me) {
                    var dTable = new YAHOO.widget.DataTable(this.id + "-grid", columnDefinitions, this.widgets.dataSource,
                            {
                                initialLoad: false,
                                dynamicData: false,
                                "MSG_EMPTY": this.msg("message.empty"),
                                "MSG_ERROR": this.msg("message.error"),
                                "MSG_LOADING": this.msg("message.loading"),
                                paginator: this.widgets.paginator
                            });

                    // Обновляем значения totalRecords данными из ответа сервера
                    dTable.handleDataReturnPayload = function DataGrid_handleDataReturnPayload(oRequest, oResponse, oPayload) {
                        me.totalRecords = oResponse.meta.totalRecords;
                        if (oPayload) {
                            oPayload.totalRecords = oResponse.meta.totalRecords;
                            oPayload.pagination.recordOffset = oResponse.meta.startIndex;
                            return oPayload
                        } else {
                            oResponse.meta.pagination =
                            {
                                rowsPerPage: me.options.pageSize,
                                recordOffset: oResponse.meta.startIndex
                            };
                            return oResponse.meta;
                        }
                    };

                    // Override abstract function within DataTable to set custom error message
                    dTable.doBeforeLoadData = function DataGrid_doBeforeLoadData(sRequest, oResponse, oPayload) {
                        if (oResponse.error) {
                            try {
                                var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                                me.widgets.dataTable.set("MSG_ERROR", response.message);
                            }
                            catch (e) {
                                me._setDefaultDataTableErrors(me.widgets.dataTable);
                            }
                        }

                        // We don't get an renderEvent for an empty recordSet, but we'd like one anyway
                        if (oResponse.results && oResponse.results.length === 0) {
                            this.fireEvent("renderEvent",
                                    {
                                        type: "renderEvent"
                                    });
                        }

                        // Must return true to have the "Loading..." message replaced by the error message
                        return true;
                    };

                    // Override default function so the "Loading..." message is suppressed
                    dTable.doBeforeSortColumn = function DataGrid_doBeforeSortColumn(oColumn, sSortDir) {
                        me.currentSort =
                        {
                            oColumn: oColumn,
                            sSortDir: sSortDir

                        };
                        me.sort = {
                            enable: true
                        }
                        return true;
                    };

                    // Сортировка. Событие при нажатии на название столбца.
                    dTable.subscribe("beforeRenderEvent", this.beforeRenderFunction.bind(this), dTable, true);

                    // Rendering complete event handler
                    dTable.subscribe("renderEvent", function () {
                        Alfresco.logger.debug("DataTable renderEvent");
                        YAHOO.Bubbling.fire("GridRendered");
                        if (me._hasEventInterest("sourcesList")) {
                            var nodeRef = "NOT_LOAD";
                            var buttonToolbarDisable = true;
                            var selectItem = me.widgets.dataTable.getSelectedTrEls()[0];
                            if (selectItem != undefined) {
                                var numItems = me.widgets.dataTable.getTrIndex(selectItem);
                                nodeRef = me.widgets.dataTable.getRecordSet().getRecord(numItems).getData().nodeRef;
                                buttonToolbarDisable = false;
                            }
                            // Отрисовка датагрида для Участников Рабочих групп
                            YAHOO.Bubbling.fire("activeGridChanged",
                                    {
                                        datagridMeta: {
                                            itemType: "lecm-rpeditor:reportDataColumn",
                                            nodeRef: nodeRef,
                                            actionsConfig: {
                                                fullDelete: true,
                                                trash: false
                                            },
                                            sort: "lecm-rpeditor:dataColumnCode|true"
                                        },
                                        bubblingLabel: "sourceColumns"
                                    });
                            if (buttonToolbarDisable) {
                                // Дективируем кнопку "Новый элемент" в правом TollBar-е
                                YAHOO.Bubbling.fire("refreshButtonState", {
                                    bubblingLabel: "sourceColumns",
                                    disabledButtons: ["activeOnParentTableClick"]
                                });
                            } else {
                                YAHOO.Bubbling.fire("refreshButtonState", {
                                    bubblingLabel: "sourceColumns",
                                    enabledButtons: ["activeOnParentTableClick"]
                                });
                            }
                        }
                    }, this, true);

                    // Enable row highlighting
                    dTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
                    dTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);

                    if (this.options.height != null) {
                        YAHOO.util.Dom.setStyle(this.id + "-grid", "height", this.options.height + "px");
                    }

                    return dTable;
                },
                setupDataTable: function (columns) {
                    // YUI DataTable colum
                    var columnDefinitions = this.getDataTableColumnDefinitions();
                    // DataTable definition
                    var me = this;
                    if (!this.widgets.dataTable) {
                        this.widgets.dataTable = this._setupDataTable(columnDefinitions, me);
                        this.widgets.dataTable.subscribe("beforeRenderEvent", function () {
                                    me.beforeRenderFunction();
                                    YAHOO.Bubbling.fire("refreshButtonState", {
                                        bubblingLabel: "sourceColumns",
                                        disabledButtons: ["activeOnParentTableClick"]
                                    });
                                },
                                me.widgets.dataTable, true);

                        this.widgets.dataTable.subscribe("rowClickEvent", this.onEventSelectRow, this, true);
                    }
                    // initialize Search
                    this.search = new LogicECM.AdvancedSearch(this.id, this).setOptions({
                        showExtendSearchBlock: false
                    });

                    var searchConfig = this.datagridMeta.searchConfig;
                    var sort = this.datagridMeta.sort;
                    var searchShowInactive = false;
                    if (searchConfig) { // Поиск через SOLR
                        if (searchConfig.formData) {
                            searchConfig.formData.datatype = this.datagridMeta.itemType;
                        } else {
                            searchConfig.formData = {
                                datatype: this.datagridMeta.itemType
                            };
                        }
                        //при первом поиске сохраняем настройки
                        if (this.initialSearchConfig == null) {
                            this.initialSearchConfig = {fullTextSearch: null};
                            this.initialSearchConfig = YAHOO.lang.merge(searchConfig, this.initialSearchConfig);
                        }

                        this.search.performSearch({
                            parent: this.datagridMeta.nodeRef,
                            searchConfig: searchConfig,
                            searchShowInactive: searchShowInactive,
                            sort: sort
                        });
                    } else { // Поиск без использования SOLR
                        this.search.performSearch({
                            parent: this.datagridMeta.nodeRef,
                            itemType: this.datagridMeta.itemType,
                            searchShowInactive: searchShowInactive,
                            sort: sort
                        });
                    }
                },
                /**
                 * Выделение строки в таблице
                 */
                onEventSelectRow: function DataGrid_onEventSelectRow(oArgs) {
                    // Выделяем строку в DataGrid
                    this.widgets.dataTable.onEventSelectRow(oArgs);
                    // Номер строки в таблице
                    var numSelectItem = this.widgets.dataTable.getTrIndex(oArgs.target);
                    // Выбранный элемент
                    var selectItem = this.widgets.dataTable.getRecordSet().getRecord(numSelectItem);
                    // Отрисовка датагрида для Столбцов Набора данных
                    YAHOO.Bubbling.fire("activeGridChanged",
                            {
                                datagridMeta: {
                                    itemType: "lecm-rpeditor:reportDataColumn",
                                    nodeRef: selectItem.getData().nodeRef,
                                    actionsConfig: {
                                        fullDelete: true,
                                        trash: false
                                    },
                                    sort: "lecm-rpeditor:dataColumnCode|true"
                                },
                                bubblingLabel: "sourceColumns"
                            });
                    // Активируем кнопку "Новый элемент" в правом TollBar-е
                    YAHOO.Bubbling.fire("refreshButtonState", {
                        bubblingLabel: "sourceColumns",
                        enabledButtons: ["activeOnParentTableClick"]
                    });
                }
            }, true);

            function createDatagrid() {
                var datagrid = new LogicECM.module.ReportsEditor.SourcesGrid('sourcesList-grid').setOptions(
                        {
                            usePagination: true,
                            useDynamicPagination: false,
                            showExtendSearchBlock: false,
                            showActionColumn: true,
                            actions: [
                                {
                                    type: "datagrid-action-link-sourcesList",
                                    id: "onActionEdit",
                                    permission: "edit",
                                    label: "${msg("actions.edit")}"
                                },
                                {
                                    type: "datagrid-action-link-sourcesList",
                                    id: "onActionDelete",
                                    permission: "delete",
                                    label: "${msg("actions.delete-row")}",
                                    evaluator: function (rowData) {
                                        var itemData = rowData.itemData;
                                        return this.isActiveItem(itemData);
                                    }
                                }
                            ],
                            bubblingLabel: "sourcesList",
                            showCheckboxColumn: false
                        }).setMessages(${messages});

                YAHOO.util.Event.onContentReady('sourcesList-grid', function () {
                    YAHOO.Bubbling.fire("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-rpeditor:reportDataSource",
                            nodeRef: LogicECM.module.ReportsEditor.SETTINGS.sourcesContainer,
                            actionsConfig: {
                                fullDelete: true
                            }
                        },
                        bubblingLabel: "sourcesList"
                    });
                });
            }

            function init() {
                createDatagrid();
            }

            YAHOO.util.Event.onDOMReady(init);
//]]></script>
</@grid.datagrid>
            </div>
        </div>
    </div>
</div>
</div>