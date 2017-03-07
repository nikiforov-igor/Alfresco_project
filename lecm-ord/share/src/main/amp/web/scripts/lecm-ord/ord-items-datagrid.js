/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */

LogicECM.ORD = LogicECM.ORD || {};

(function () {
    LogicECM.ORD.PointsTS = function (htmlId) {
        LogicECM.ORD.PointsTS.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.ORD.PointsTS, LogicECM.module.DocumentTable);

    YAHOO.lang.augmentObject(LogicECM.ORD.PointsTS.prototype, {

        createDataGrid: function () {
            var actions = [];
            var actionType = "datagrid-action-link-" + this.options.bubblingLabel;
            var currentUser = {
                isController: false,
                nodeRef: null,
                roles: []
            };
            var allowedStatuses = ["Черновик", "Проект", "Согласован", "На доработке", "Подписан", "На регистрации"];

            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/ord/items/getInfo",
                dataObj: {
                    nodeRef: this.options.itemId
                },
                successCallback: {
                    fn: function (response) {
                        if(response && response.json.user){
                            if(response.json.user.roles){
                                currentUser.roles = response.json.user.roles;
                            }
                            currentUser.nodeRef = response.json.user.nodeRef;
                            currentUser.isController = response.json.user.isController;
                            var docStatus = response.json.document.status;
                            actions.push({
                                type: actionType,
                                id: "onActionCompletePoint",
                                permission: "edit",
                                label: this.msg("ord.item.complete.button"),
                                evaluator: this.showCompleteActionEvaluator
                            });
                            actions.push({
                                type: actionType,
                                id: "onActionExecutePoint",
                                permission: "edit",
                                label: this.msg("ord.item.execute.button"),
                                evaluator: this.showExecuteActionEvaluator
                            });
                            if (allowedStatuses.includes(docStatus) &&
                                ((docStatus == "На регистрации" && currentUser.roles.includes("DA_REGISTRAR_DYN") && currentUser.roles.includes("DA_REGISTRARS")) ||
                                (docStatus != "На регистрации" && currentUser.roles.includes("BR_INITIATOR")))) {
                                if (!this.options.disabled && this.options.mode == "edit") {
                                    if (this.options.allowEdit === true) {
                                        actions.push({
                                            type: actionType,
                                            id: "onActionEdit",
                                            permission: "edit",
                                            label: this.msg("actions.edit"),
                                            evaluator: this.showActionsEvaluator
                                        });
                                    }
                                    if (this.options.allowDelete === true) {
                                        actions.push({
                                            type: actionType,
                                            id: "onActionDelete",
                                            permission: "delete",
                                            label: this.msg("actions.delete-row"),
                                            evaluator: this.showActionsEvaluator
                                        });
                                    }
                                }

                                if (!this.options.isTableSortable && this.options.showActions && this.options.mode == "edit" && !this.options.disabled) {
                                    var otherActions = [];
                                    if (this.options.allowEdit === true) {
                                        otherActions.push({
                                            type: actionType,
                                            id: "onMoveTableRowUp",
                                            permission: "edit",
                                            label: this.msg("actions.tableRowUp"),
                                            evaluator: this.showActionsEvaluator
                                        });
                                        otherActions.push({
                                            type: actionType,
                                            id: "onMoveTableRowDown",
                                            permission: "edit",
                                            label: this.msg("action.tableRowDown"),
                                            evaluator: this.showActionsEvaluator
                                        });
                                    }
                                    if (this.options.allowCreate === true) {
                                        otherActions.push({
                                            type: actionType,
                                            id: "onAddRow",
                                            permission: "edit",
                                            label: this.msg("action.addRow"),
                                            evaluator: this.showActionsEvaluator
                                        });
                                    }
                                    actions = actions.concat(otherActions);
                                }
                            } else {
                                Dom.setStyle(this.id + "-toolbar", "display", "none");
                            }
                            this.realCreateDatagrid(actions, currentUser, docStatus, allowedStatuses);
                        }
                    },
                    scope: this
                },
                failureMessage: Alfresco.util.message("message.details.failure"),
                scope: this
            });
        },

        realCreateDatagrid: function (actions, currentUser, docStatus, allowedStatuses) {
            if (this.tableData != null && this.tableData.rowType != null) {
                var expandable = !allowedStatuses.includes(docStatus);

                var datagrid = new LogicECM.ORD.PointsDatagrid(this.options.containerId).setOptions({
                    usePagination: true,
                    showExtendSearchBlock: false,
                    formMode: this.options.mode,
                    actions: actions,
                    splitActionsAt: actions.length,
                    currentUser: currentUser,
                    currentDocStatus: docStatus,
                    documentNodeRef: this.options.itemId,
                    editForm: "edit",
                    datagridMeta: {
                        itemType: this.tableData.rowType,
                        datagridFormId: this.options.datagridFormId,
                        createFormId: "",
                        nodeRef: this.tableData.nodeRef,
                        parent: this.tableData.nodeRef,
                        actionsConfig: {
                            fullDelete: true
                        },
                        sort: "lecm-document:indexTableRow",
                        useChildQuery: false,
                        searchConfig: {}
                    },
                    showCheckboxColumn: false,
                    bubblingLabel: this.options.bubblingLabel,
                    showActionColumn: true,
                    attributeForShow: this.options.attributeForShow,
                    pageSize: this.tableData.pageSize != null && this.tableData.pageSize > 0 ? this.tableData.pageSize : 10,
                    useCookieForSort: false,
                    overrideSortingWith: this.options.isTableSortable,
                    refreshAfterCreate: this.options.refreshAfterCreate,
                    editFormTitleMsg: this.options.editFormTitleMsg,
                    createFormTitleMsg: this.options.createFormTitleMsg,
                    viewFormTitleMsg: this.options.viewFormTitleMsg,
                    expandable: expandable,
                    expandDataSource: this.options.expandDataSource,
                    excludeColumns: ["lecm-ord-table-structure:controller-assoc"],
                    showOtherActionColumn: true
                }).setMessages(this.options.messages);
            }

            if (this.tableData != null) {
                datagrid.tableDataNodeRef = this.tableData.nodeRef;
            }
            datagrid.deleteMessageFunction = this.options.deleteMessageFunction;
            datagrid.draw();
            this.datagrid = datagrid;
        },

        showCompleteActionEvaluator: function (rowData) {
            var itemStatus = rowData.itemData["assoc_lecm-ord-table-structure_item-status-assoc"];
            var controller = rowData.itemData["assoc_lecm-ord-table-structure_controller-assoc"];
            var executor = rowData.itemData["assoc_lecm-ord-table-structure_executor-assoc"];
            var isStatusOk = itemStatus && itemStatus.displayValue == "На исполнении";
            var isEmployeeOk = (this.options.currentUser.isController || (controller && this.options.currentUser.nodeRef == controller.value)) && (executor && this.options.currentUser.nodeRef != executor.value);
            return isStatusOk && isEmployeeOk;
        },
        showExecuteActionEvaluator: function (rowData) {
            var itemStatus = rowData.itemData["assoc_lecm-ord-table-structure_item-status-assoc"];
            var controller = rowData.itemData["assoc_lecm-ord-table-structure_controller-assoc"];
            var executor = rowData.itemData["assoc_lecm-ord-table-structure_executor-assoc"];
            var reportRequired = rowData.itemData["prop_lecm-ord-table-structure_report-required"];
            var isReposrtRequired = reportRequired && reportRequired.value;
            var isStatusOk = itemStatus && itemStatus.displayValue == "На исполнении";
            var isEmployeeOk = !isReposrtRequired && ((this.options.currentUser.nodeRef == executor.value) || ((this.options.currentUser.isController && this.options.currentUser.nodeRef == executor.value) || (controller && executor.value == controller.value)));
            return isStatusOk && isEmployeeOk;
        },
        showActionsEvaluator: function (rowData) {
            var itemStatus = rowData.itemData["assoc_lecm-ord-table-structure_item-status-assoc"];
            var isItemStatusOk = itemStatus && itemStatus.displayValue == "Ожидает исполнения";
            var isEmployeeOk = (this.options.currentDocStatus == "На регистрации" && this.options.currentUser.roles.includes("DA_REGISTRAR_DYN") && this.options.currentUser.roles.includes("DA_REGISTRARS")) ||
                (this.options.currentDocStatus != "На регистрации" && this.options.currentUser.roles.includes("BR_INITIATOR"));
            return isItemStatusOk && isEmployeeOk;
        }

    }, true);
})();

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;
    var $html = Alfresco.util.encodeHTML,
        $links = Alfresco.util.activateLinks,
        $combine = Alfresco.util.combinePaths,
        $userProfile = Alfresco.util.userProfileLink;

    LogicECM.ORD.PointsDatagrid = function (htmlId) {

        Bubbling.on("onActionCompletePoint", function (layer, args) {
            this.onActionCompletePoint({
                nodeRef: args[1].nodeRef
            });
        }, this, true);
        Bubbling.on("onActionExecutePoint", function (layer, args) {
            this.onActionExecutePoint({
                nodeRef: args[1].nodeRef
            });
        }, this, true);

        return LogicECM.ORD.PointsDatagrid.superclass.constructor.call(this, htmlId);
    };

    YAHOO.lang.extend(LogicECM.ORD.PointsDatagrid, LogicECM.module.DocumentTableDataGrid);

    YAHOO.lang.augmentObject(LogicECM.ORD.PointsDatagrid.prototype, {

        onExpand: function(record, isExpandAutomatically) {
            Alfresco.util.Ajax.jsonPost({
                url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                dataObj: {
                    nodeRef: record.getData("nodeRef"),
                    substituteString: "{lecm-ord-table-structure:errand-assoc-ref}"
                },
                successCallback: {
                    fn: function (response) {
                        if (response && response.json.formatString) {
                            var nodeRef = response.json.formatString;
                            this.showExpandForm(record, isExpandAutomatically, nodeRef);
                        }
                    },
                    scope: this
                },
                failureMessage: Alfresco.util.message('message.failure'),
                scope: this
            });
        },
        showExpandForm: function(record, isExpandAutomatically, nodeRef){
            if (!this.doubleClickLock) {
                this.doubleClickLock = {};
            } else if (this.doubleClickLock[record.getId()]) {
                return;
            }
            this.doubleClickLock[record.getId()] = true;

            if (nodeRef) {
                var dataObj = YAHOO.lang.merge({
                    htmlid: this.getExpandedFormId(record),
                    itemKind: "node",
                    itemId: nodeRef,
                    mode: "view",
                    isExpandAutomatically: isExpandAutomatically
                }, this.options.expandDataObj);
                Alfresco.util.Ajax.request({
                    url: this.getExpandUri(),
                    dataObj: dataObj,
                    successCallback: {
                        scope: this,
                        fn: function(response) {
                            if (response.serverResponse != null) {
                                this.addExpandedRow(record, response.serverResponse.responseText);
                            }
                            this.doubleClickLock[record.getId()] = false;
                        }
                    },
                    failureMessage: this.msg('message.failure'),
                    execScripts: true,
                    scope: this
                });
            }
        },
        onActionCompletePoint: function (me, asset, owner, actionsConfig, confirmFunction) {
            var nodeRef = arguments[0].nodeRef;
            if (nodeRef) {
                var formId = "ord-complete-item";
                var completePointDialog = new Alfresco.module.SimpleDialog(this.id + '-' + formId);
                completePointDialog.setOptions({
                    templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
                    actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/ord/item/complete?nodeRef=' + nodeRef,
                    templateRequestParams: {
                        formId: formId,
                        itemKind: "node",
                        itemId: nodeRef,
                        mode: "edit",
                        showCancelButton: true,
                        showCaption: false,
                        submitType: 'json'
                    },
                    width: '50em',
                    zIndex: 2000,
                    destroyOnHide: true,
                    doBeforeDialogShow: {
                        fn: function (form, simpleDialog) {
                            simpleDialog.dialog.setHeader(this.msg("ord.item.completion.form.title"));
                            simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
                                LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
                                LogicECM.module.Base.Util.formDestructor(event, args, params);
                            }, {moduleId: simpleDialog.id}, this);
                        },
                        scope: this
                    },
                    onSuccess: {
                        fn: function (response) {
                            this._itemUpdate(nodeRef);
                            completePointDialog.hide();
                        },
                        scope: this
                    },
                    onFailure: {
                        fn: function (response) {
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text: Alfresco.util.message("message.details.failure")
                                });
                            completePointDialog.hide();
                        }
                    }
                });
                completePointDialog.show();
            }
        },
        onActionExecutePoint: function (me, asset, owner, actionsConfig, confirmFunction) {
            var scope = this;
            var nodeRef = arguments[0].nodeRef;
            var executeDialog = new YAHOO.widget.SimpleDialog(this.id + '-execute-point-dialog-panel', {
                visible: false,
                draggable: true,
                close: false,
                fixedcenter: true,
                constraintoviewport: true,
                destroyOnHide: true,
                buttons: [
                    {
                        text: Alfresco.util.message("button.resume"),
                        handler: function () {
                            Alfresco.util.Ajax.jsonGet({
                                url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/ord/item/execute?nodeRef=' + nodeRef,
                                successCallback: {
                                    fn: function (response) {
                                        if (response.json.success) {
                                            this._itemUpdate(nodeRef);
                                        } else {
                                            Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text: Alfresco.util.message("message.details.failure")
                                                });
                                        }
                                    },
                                    scope: scope
                                },
                                failureMessage: Alfresco.util.message("message.details.failure"),
                                scope: scope
                            });
                            executeDialog.hide();
                        },
                        isDefault: true
                    },
                    {
                        text: Alfresco.util.message("button.cancel"),
                        handler: function () {
                            executeDialog.hide();
                        }
                    }
                ]
            });
            executeDialog.setHeader(Alfresco.util.message("ord.item.execute.form.title"));
            executeDialog.setBody("</br><p> " + Alfresco.util.message("ord.item.execute.form.message") + " </p></br>");
            executeDialog.render(document.body);
            executeDialog.show();
        },

        getDataTableColumnDefinitions: function DataGrid_getDataTableColumnDefinitions() {
            var columnDefinitions = LogicECM.module.DocumentTableDataGrid.prototype.getDataTableColumnDefinitions.call(this);

            var column, sortable;
            for (var i = 0, ii = columnDefinitions.length; i < ii; i++) {
                column = columnDefinitions[i];
                if (column.key == "prop_lecm-ord-table-structure_title") {
                    column.width = 200;
                    column.formatter = this.getOrdColumnFormatter(column.dataType);
                }
                if (column.key == "prop_lecm-ord-table-structure_report-required") {
                    column.formatter = this.getOrdColumnFormatter(column.dataType);
                }
            }
            return columnDefinitions;
        },
        getOrdColumnFormatter: function () {
            var scope = this;
            return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData) {
                var columnContent = "";
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
                        var datalistColumn = scope.datagridColumns[oColumn.key];
                        if (datalistColumn) {
                            oData = YAHOO.lang.isArray(oData) ? oData : [oData];
                            for (var i = 0, ii = oData.length, data; i < ii; i++) {
                                data = oData[i];
                                switch (datalistColumn.dataType.toLowerCase()) {
                                    case "text":
                                        var hexColorPattern = /^#[0-9a-f]{6}$/i;
                                        if (data.displayValue.indexOf("!html ") == 0) {
                                            columnContent += data.displayValue.substring(6);
                                        } else if (hexColorPattern.test(data.displayValue)) {
                                            columnContent += $links(data.displayValue + '<div class="color-block" style="background-color: ' + data.displayValue + ';">&nbsp</div>');
                                        } else {
                                            columnContent += $links($html(data.displayValue));
                                        }
                                        columnContent = LogicECM.module.Base.Util.getCroppedItem(columnContent);
                                        break;

                                    case "boolean":
                                        columnContent += '<div class="centered">';
                                        columnContent += (data.value ? scope.msg("message.yes") : scope.msg("message.no"));
                                        columnContent += '</div>';
                                        break;
                                }
                                if (i < ii - 1) {
                                    columnContent += "<br />";
                                }
                            }
                        }
                    }
                }
                elCell.innerHTML = columnContent;
            };
        }

    }, true)

})();
