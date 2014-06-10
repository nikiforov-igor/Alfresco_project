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
        onRenderEvent: function () {
            var me = this;
            if (this._hasEventInterest("workGroup")) {
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
                                trash: false
                            }
                        },
                        bubblingLabel: "workForce"
                    });
                if (buttonToolbarDisable) {
                    // Дективируем кнопку "Новый элемент" в правом TollBar-е
                    YAHOO.Bubbling.fire("refreshButtonState", {
                        bubblingLabel: "workForce",
                        disabledButtons: ["activeOnParentTableClick"]
                    });
                } else {
                    YAHOO.Bubbling.fire("refreshButtonState", {
                        bubblingLabel: "workForce",
                        enabledButtons: ["activeOnParentTableClick"]
                    });
                }
            }
            YAHOO.Bubbling.fire("GridRendered");
            // Deferred functions specified?
            for (var i = 0, j = this.afterDataGridUpdate.length; i < j; i++) {
                this.afterDataGridUpdate[i].call(this);
            }
            this.afterDataGridUpdate = [];
        },
        setupDataTable: function () {
            var columnDefinitions = this.getDataTableColumnDefinitions();
            // DataTable definition
            var me = this;
            if (!this.widgets.dataTable) {
                this._setupPaginatior();
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
            var searchShowInactive;
            if (this.datagridMeta.hasOwnProperty ("searchShowInactive")) {
                searchShowInactive = this.datagridMeta.searchShowInactive;
            } else {
                searchShowInactive = this.options.searchShowInactive;
            }

            if (!searchConfig) {
                searchConfig = {};

            }
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
                    itemType: this.datagridMeta.itemType,
                searchConfig: searchConfig,
                    searchShowInactive: searchShowInactive,
                    sort:sort
                });
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
                            trash: false
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
