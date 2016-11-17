if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.Base = LogicECM.module.Base || {};
LogicECM.module.Contracts = LogicECM.module.Contracts || {};

(function () {

    LogicECM.module.Contracts.contractStagesTable = function (htmlId) {
        return LogicECM.module.Contracts.contractStagesTable.superclass.constructor.call(this, htmlId);
    };

    YAHOO.extend(LogicECM.module.Contracts.contractStagesTable, LogicECM.module.DocumentTable, {
        createDataGrid: function() {
            if (this.tableData != null && this.tableData.rowType != null) {
                var actions = [];
                var actionType = "datagrid-action-link-" + this.options.bubblingLabel;
                if (!this.options.disabled && this.options.mode=="edit") {
                    if (this.options.allowEdit===true) {
                        actions.push({
                            type: actionType,
                            id: "onActionEdit",
                            permission: "edit",
                            label: this.msg("actions.edit")
                        });
                    }
                    if (this.options.allowDelete===true) {
                        actions.push({
                            type: actionType,
                            id: "onActionDelete",
                            permission: "delete",
                            label: this.msg("actions.delete-row")
                        });
                    }
                }
                var splitActionAt = actions.length;

                if (!this.options.isTableSortable && this.options.showActions && this.options.mode=="edit" && !this.options.disabled) {
                    var otherActions = [];
                    if (this.options.allowEdit===true) {
                        otherActions.push({
                            type: actionType,
                            id: "onMoveTableRowUp",
                            permission: "edit",
                            label: this.msg("actions.tableRowUp")
                        });
                        otherActions.push({
                            type: actionType,
                            id: "onMoveTableRowDown",
                            permission: "edit",
                            label: this.msg("action.tableRowDown")
                        });
                    }
                    if (this.options.allowCreate===true) {
                        otherActions.push({
                            type: actionType,
                            id: "onAddRow",
                            permission: "edit",
                            label: this.msg("action.addRow")
                        });
                    }
                    actions = actions.concat(otherActions);
                    splitActionAt = actions.length;
                }

                var datagrid = new LogicECM.module.Contracts.contractStagesTableDataGrid(this.options.containerId).setOptions({
                    usePagination: true,
                    showExtendSearchBlock: false,
                    formMode: this.options.mode,
                    actions: actions,
                    splitActionsAt: splitActionAt,
                    datagridMeta: {
                        useFilterByOrg: false,
                        itemType: this.tableData.rowType,
                        datagridFormId: this.options.datagridFormId,
                        createFormId: "",
                        nodeRef: this.tableData.nodeRef,
                        actionsConfig: {
                            fullDelete: true
                        },
                        sort: this.options.sort ? this.options.sort : "lecm-document:indexTableRow",
                        useChildQuery: true
                    },
                    bubblingLabel: this.options.bubblingLabel,
                    showActionColumn: this.options.showActions,
                    showOtherActionColumn: true,
                    showCheckboxColumn: false,
                    attributeForShow: this.options.attributeForShow,
                    pageSize: this.tableData.pageSize != null && this.tableData.pageSize > 0 ? this.tableData.pageSize : 10,
                    useCookieForSort: false,
                    overrideSortingWith: this.options.isTableSortable,
                    refreshAfterCreate: this.options.refreshAfterCreate,
                    editFormTitleMsg: this.options.editFormTitleMsg,
                    createFormTitleMsg: this.options.createFormTitleMsg,
                    viewFormTitleMsg: this.options.viewFormTitleMsg,
                    dataSource: this.options.dataSource,
                    expandable: this.options.expandable,
                    expandDataSource: this.options.expandDataSource,
                    createItemBtnMsg: this.options.createItemBtnMsg
                }).setMessages(this.options.messages);
            }

            if (this.tableData != null) {
                datagrid.tableDataNodeRef = this.tableData.nodeRef;
            }
            datagrid.deleteMessageFunction = this.options.deleteMessageFunction;
            datagrid.draw();
        }
    });

    LogicECM.module.Contracts.contractStagesTableDataGrid = function (htmlId) {
        return LogicECM.module.Contracts.contractStagesTableDataGrid.superclass.constructor.call(this, htmlId);
    };

    YAHOO.lang.extend(LogicECM.module.Contracts.contractStagesTableDataGrid, LogicECM.module.DocumentTableDataGrid, {
        getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {
            var html = "";
            if (!oRecord) {
                oRecord = this.getRecord(elCell);
            }
            if (!oColumn) {
                oColumn = this.getColumn(elCell.parentNode.cellIndex);
            }

            if (oRecord && oColumn) {
                if (!oData) {
                    oData = oRecord.getData("itemData")[oColumn.field];
                }

                if (oData) {
                    var datalistColumn = grid.datagridColumns[oColumn.key];
                    if (datalistColumn) {
                        oData = YAHOO.lang.isArray(oData) ? oData : [oData];
                        for (var i = 0, ii = oData.length, data; i < ii; i++) {
                            data = oData[i];

                            //статус
                            if (datalistColumn.name.toLowerCase().indexOf("status") > -1) {
                                var tr = elCell.parentElement.parentElement;
                                var children = tr.children;
                                for (var i = 0; i < children.length; i++) {
                                    children[i].setAttribute("status", data.value);
                                }

                            }

                            var columnContent = "";
                            switch (datalistColumn.dataType.toLowerCase()) { //  меняем отрисовку для конкретных колонок
                                case "cm:content":
                                    var fileIcon = Alfresco.util.getFileIcon(data.displayValue, "cm:content", 16);
                                    var fileIconHtml = "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + fileIcon +"' width='16' height='16'/>";

                                    columnContent = "<a href='" + Alfresco.constants.URL_PAGECONTEXT+"document-attachment?nodeRef="+ data.value +"' title='" + data.displayValue + "'>" + fileIconHtml + "</a>";
                                    break;
                                case "cm:cmobject":
                                    columnContent = "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + data.value + "\', title: \'logicecm.view\'})\">" + data.displayValue + "</a>";
                                    break;
                                default:
                                    break;
                            }
                            if (columnContent != "") {
                                html += columnContent;

                                if (i < ii - 1) {
                                    html += "<br />";
                                }
                            }
                        }
                    }
                }
            }
            return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
        }
    });

    
})();