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
 * @class LogicECM.module.Orgstructure
 */
LogicECM.module.Orgstructure = LogicECM.module.Orgstructure || {};

(function () {

    LogicECM.module.Orgstructure.WorkGroupDataGrid = function (containerId) {
        return LogicECM.module.Orgstructure.WorkGroupDataGrid.superclass.constructor.call(this, containerId);
    };

    /**
     * Extend from LogicECM.module.Base.WorkGroupDataGrid
     */
    YAHOO.lang.extend(LogicECM.module.Orgstructure.WorkGroupDataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Orgstructure.WorkGroupDataGrid.prototype, {
        /** Прорисовка таблицы, установка свойств, сортировка.
         * @param columnDefinitions колонки
         * @param me {object} this
         * @return {YAHOO.widget.DataTable} таблица
         * @private
         */
        _setupDataTable: function (columnDefinitions, me) {
            var dTable = new YAHOO.widget.DataTable(this.id + "-grid", columnDefinitions, this.widgets.dataSource,
                {
                    renderLoopSize: this.options.usePagination ? 16 : 32,
                    initialLoad: false,
                    dynamicData: false,
                    "MSG_EMPTY": this.msg("message.empty"),
                    "MSG_ERROR": this.msg("message.error"),
                    paginator: this.widgets.paginator
                });

            // Update totalRecords with value from server
            dTable.handleDataReturnPayload = function DataGrid_handleDataReturnPayload(oRequest, oResponse, oPayload) {
                me.totalRecords = oResponse.meta.totalRecords;
                oResponse.meta.pagination =
                {
                    rowsPerPage: me.options.pageSize,
                    recordOffset: (me.currentPage - 1) * me.options.pageSize
                };
                return oResponse.meta;
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
                if (oResponse.results.length === 0) {
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

            if (this.options.showCheckboxColumn) {
                // Событие когда выбранны все элементы
                YAHOO.util.Event.onAvailable(this.id + "-select-all-records", function () {
                    YAHOO.util.Event.on(this.id + "-select-all-records", 'click', this.selectAllClick, this, true);
                }, this, true);

                // File checked handler
                dTable.subscribe("checkboxClickEvent", function (e) {
                    var id = e.target.value;
                    this.selectedItems[id] = e.target.checked;

                    var checks = Selector.query('input[type="checkbox"]', dTable.getTbodyEl()),
                        len = checks.length, i;

                    var allChecked = true;
                    for (i = 0; i < len; i++) {
                        if (!checks[i].checked) {
                            allChecked = false;
                            break;
                        }
                    }
                    Dom.get(this.id + '-select-all-records').checked = allChecked;

                    Bubbling.fire("selectedItemsChanged");
                }, this, true);
            }
            // Сортировка. Событие при нажатии на название столбца.
            dTable.subscribe("beforeRenderEvent", this.beforeRenderFunction.bind(this), dTable, true);

            // Rendering complete event handler
            dTable.subscribe("renderEvent", function () {
                Alfresco.logger.debug("DataTable renderEvent");
                if (me._hasEventInterest("workGroup")) {
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
                                itemType: "lecm-orgstr:workforce",
                                nodeRef: nodeRef,
                                actionsConfig: {
                                    fullDelete: true,
                                    targetDelete: true
                                }
                            },
                            bubblingLabel: "workForce"
                        });
                    if (buttonToolbarDisable) {
                        // Дективируем кнопку "Новый элемент" в правом TollBar-е
                        YAHOO.Bubbling.fire("refreshButtonState",{
                            bubblingLabel: "workForce",
                            disabledButtons: ["activeOnParentTableClick"]
                        });
                    } else {
                        YAHOO.Bubbling.fire("refreshButtonState",{
                            bubblingLabel: "workForce",
                            enabledButtons: ["activeOnParentTableClick"]
                        });
                    }
                }
                // Deferred functions specified?
                for (var i = 0, j = this.afterDataGridUpdate.length; i < j; i++) {
                    this.afterDataGridUpdate[i].call(this);
                }
                this.afterDataGridUpdate = [];
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
                        YAHOO.Bubbling.fire("refreshButtonState",{
                            bubblingLabel: "workForce",
                            disabledButtons: ["activeOnParentTableClick"]
                        });
                    },
                    me.widgets.dataTable, true);
                if (this._hasEventInterest("workGroup")) {
                    this.widgets.dataTable.subscribe("rowClickEvent", this.onEventSelectRow, this, true);
                }
            }
            // initialize Search
            this.search = new LogicECM.AdvancedSearch(this.id, this).setOptions({
                showExtendSearchBlock: this.options.showExtendSearchBlock
            });

            var searchConfig = this.datagridMeta.searchConfig;
            var sort = this.datagridMeta.sort;
            if (searchConfig) { // Поиск через SOLR
                searchConfig.formData = {
                    datatype: this.datagridMeta.itemType
                };
                this.search.performSearch({
                    searchConfig: searchConfig,
                    searchShowInactive: false,
                    sort:sort
                });
            } else { // Поиск без использования SOLR
                this.search.performSearch({
                    parent: this.datagridMeta.nodeRef,
                    itemType: this.datagridMeta.itemType,
                    searchShowInactive: false,
                    sort:sort
                });
            }
        },
        /**
         * Выделение строки в таблице
         */
        onEventSelectRow: function DataGrid_onEventSelectRow(oArgs) {
            // Проверка а из той ли песочницы (два dataGrida) мы вызвали метод. Переопределяz метод мы
            // переопределяем его для всех песочниц на странице.
            //if (this._hasEventInterest("workGroup")) {
            // Выделяем строку в DataGrid
            this.widgets.dataTable.onEventSelectRow(oArgs);
            // Номер строки в таблице
            var numSelectItem = this.widgets.dataTable.getTrIndex(oArgs.target);
            // Выбранный элемент
            var selectItem = this.widgets.dataTable.getRecordSet().getRecord(numSelectItem);
            // Отрисовка датагрида для Участников Рабочих групп
            YAHOO.Bubbling.fire("activeGridChanged",
                {
                    datagridMeta: {
                        itemType: "lecm-orgstr:workforce",
                        nodeRef: selectItem.getData().nodeRef,
                        actionsConfig: {
                            fullDelete: true,
                            targetDelete: true
                        }
                    },
                    bubblingLabel: "workForce"
                });
            if (LogicECM.module.OrgStructure.IS_ENGINEER) {
                // Активируем кнопку "Новый элемент" в правом TollBar-е
                YAHOO.Bubbling.fire("refreshButtonState",{
                    bubblingLabel: "workForce",
                    enabledButtons: ["activeOnParentTableClick"]
                });
            }
        }
    }, true);
})();
