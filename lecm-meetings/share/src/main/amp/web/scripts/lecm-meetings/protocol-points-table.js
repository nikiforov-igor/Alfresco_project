if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.Base = LogicECM.module.Base || {};
LogicECM.module.Meetings = LogicECM.module.Meetings || {};

(function () {

    LogicECM.module.Meetings.protocolPointsTable = function (htmlId) {
        return LogicECM.module.Meetings.protocolPointsTable.superclass.constructor.call(this, htmlId);
    };

    YAHOO.extend(LogicECM.module.Meetings.protocolPointsTable, LogicECM.module.DocumentTable, {

        createDataGrid: function () {
            if (this.tableData != null && this.tableData.rowType != null) {
                var actions = [];
                var actionType = "datagrid-action-link-" + this.options.bubblingLabel;
                if (!this.options.disabled && this.options.mode == "edit") {
                    if (this.options.allowEdit === true) {
                        actions.push({
                            type: actionType,
                            id: "onActionEdit",
                            permission: "edit",
                            label: this.msg("actions.edit")
                        });
                    }
                    if (this.options.allowDelete === true) {
                        actions.push({
                            type: actionType,
                            id: "onActionDelete",
                            permission: "delete",
                            label: this.msg("actions.delete-row")
                        });
                    }
                }
                var splitActionAt = actions.length;

                if (!this.options.isTableSortable && this.options.showActions && this.options.mode == "edit" && !this.options.disabled) {
                    var otherActions = [];
                    if (this.options.allowEdit === true) {
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
                    if (this.options.allowCreate === true) {
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

                var datagrid = new LogicECM.module.Meetings.protocolPointsTableDataGrid(this.options.containerId).setOptions({
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
                    createItemBtnMsg: this.options.createItemBtnMsg,
                    itemId: this.options.itemId,
                    reportersFilterEnabled: true
                }).setMessages(this.options.messages);

                if (this.tableData != null) {
                    datagrid.tableDataNodeRef = this.tableData.nodeRef;
                }
                datagrid.deleteMessageFunction = this.options.deleteMessageFunction;

                Alfresco.util.Ajax.jsonGet({
                    url: Alfresco.constants.PROXY_URI + 'lecm/document/connections/api/getConnectionsWithDocument',
                    dataObj: {
                        documentNodeRef: this.options.itemId
                    },
                    successCallback: {
                        scope: this,
                        fn: function (response) {
                            var oResults = JSON.parse(response.serverResponse.responseText);

                            if (oResults && oResults.items && oResults.items.length) {

                                if (oResults.items.some(function (item) {
                                    return item.primaryDocument.type == 'lecm-meetings:document';
                                }, this)) {
                                    datagrid.setOptions({
                                        reportersFilterEnabled: false
                                    })
                                }
                            }

                            datagrid.draw();
                        }
                    },
                    failureCallback: {
                        scope: this,
                        fn: function () {
                            datagrid.draw();
                        }
                    }
                });
            }



        }

    }, true);

    LogicECM.module.Meetings.protocolPointsTableDataGrid = function (htmlId) {
        LogicECM.module.Meetings.protocolPointsTableDataGrid.superclass.constructor.call(this, htmlId);

        this.options.formId = htmlId.substring(0, htmlId.indexOf("_assoc"));

        return this
    };

    YAHOO.lang.extend(LogicECM.module.Meetings.protocolPointsTableDataGrid, LogicECM.module.DocumentTableDataGrid, {

        fieldsForFilter: [
            '_assoc_lecm-protocol_meeting-chairman-assoc',
            '_assoc_lecm-protocol_secretary-assoc',
            '_assoc_lecm-protocol_attended-assoc',
            '_assoc_lecm-document_author-assoc'
        ],

        filteredFields: ['lecm-protocol-ts:reporter-assoc', 'lecm-protocol-ts:coreporter-assoc'],

        reportersFilter: [],

        notExistsInFilter: function (item) {
            return this.reportersFilter.indexOf(item) == -1 && item.length > 0;
        },

        getAllowedEmployees: function () {
            this.reportersFilter = [];

            this.fieldsForFilter.forEach(function (fieldId) {
                var controls = Alfresco.util.ComponentManager.find({id: this.options.formId + fieldId});

                if (controls && controls.length) {
                    this.reportersFilter = this.reportersFilter.concat(Object.keys(controls[0].selectedItems).filter(this.notExistsInFilter, this));
                } else {
                    var control = YAHOO.util.Dom.get(this.options.formId + fieldId);
                    if (control) {
                        this.reportersFilter = this.reportersFilter.concat((control.value.split(",")).filter(this.notExistsInFilter, this));
                    }
                }
            }, this);

            jQuery.ajax({
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/workflow/routes/getEmployeesOfAllDocumentRoutes",
                type: "GET",
                timeout: 30000,
                async: false,
                dataType: "json",
                contentType: "application/json",
                data: {
                    nodeRef: this.options.itemId
                },
                context:this,
                processData: true,
                success: function (response) {
                    if (response && response.employees && response.employees.length) {
                        this.reportersFilter = this.reportersFilter.concat(response.employees.filter(this.notExistsInFilter, this));
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    Alfresco.util.PopupManager.displayMessage({
                        text: errorThrown
                    });
                }
            });

            return this.reportersFilter.join(",");
        },

        onActionEdit: function DataGrid_onActionEdit(item) {
            if (this.editDialogOpening) {
                return;
            }
            this.editDialogOpening = true;
            var me = this;

            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
            var templateRequestParams = {
                itemKind: "node",
                itemId: item.nodeRef,
                mode: "edit",
                submitType: "json",
                showCancelButton: true,
				showCaption: false
            };
            if (this.options.reportersFilterEnabled) {
                templateRequestParams.args = JSON.stringify({
                    allowedForPoint: this.getAllowedEmployees()
                });
            }
            if (this.options.editForm) {
                templateRequestParams.formId = this.options.editForm;
            }

            // Using Forms Service, so always create new instance
            var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails" + Alfresco.util.generateDomId());
            editDetails.setOptions({
                width: this.options.editFormWidth,
                templateUrl: templateUrl,
                templateRequestParams: templateRequestParams,
                actionUrl: null,
                destroyOnHide: true,
                doBeforeDialogShow: {
                    fn: function (p_form, p_dialog) {
                        var contId = p_dialog.id + "-form-container";
                        if (item.type && item.type != "") {
                            Dom.addClass(contId, item.type.replace(":", "_") + "_edit");
                        }
                        p_dialog.dialog.setHeader(this.msg(this.options.editFormTitleMsg));
                        this.editDialogOpening = false;

                        p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id, force: true}, this);
                    },
                    scope: this
                },
                onSuccess: {
                    fn: function (response) {
                        Bubbling.fire("datagridRefresh", {
                            bubblingLabel: me.options.bubblingLabel
                        });
                        Alfresco.util.PopupManager.displayMessage({
                            text: this.msg("message.details.success")
                        });
                        this.editDialogOpening = false;
                    },
                    scope: this
                },
                onFailure: {
                    fn: function (response) {
                        Alfresco.util.PopupManager.displayMessage({
                            text: this.msg("message.details.failure")
                        });
                        this.editDialogOpening = false;
                    },
                    scope: this
                }
            }).show();
        },

        showCreateDialog: function (meta, callback, successMessage) {
            if (this.editDialogOpening) return;
            this.editDialogOpening = true;
            var me = this;

            var doBeforeDialogShow = function (p_form, p_dialog) {
                var addMsg = meta.addMessage;
                var contId = p_dialog.id + "-form-container";
                Alfresco.util.populateHTML(
                    [contId + "_h", addMsg ? addMsg : this.msg(this.options.createFormTitleMsg)]
                );
                if (meta.itemType && meta.itemType != "") {
                    Dom.addClass(contId, meta.itemType.replace(":", "_") + "_edit");
                }
                me.editDialogOpening = false;

                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id, force: true}, this);
            };

            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
            var templateRequestParams = {
                itemKind: "type",
                itemId: meta.itemType,
                destination: meta.nodeRef,
                mode: "create",
                formId: meta.createFormId != null ? meta.createFormId : "",
                submitType: "json",
                showCancelButton: true,
				showCaption: false
            };
            if (this.options.reportersFilterEnabled) {
                templateRequestParams.args = JSON.stringify({
                    allowedForPoint: this.getAllowedEmployees()
                });
            }
            var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails" + Alfresco.util.generateDomId());
            createDetails.setOptions({
                width: "50em",
                templateUrl: templateUrl,
                templateRequestParams: templateRequestParams,
                actionUrl: null,
                destroyOnHide: true,
                doBeforeDialogShow: {
                    fn: doBeforeDialogShow,
                    scope: this
                },
                onSuccess: {
                    fn: function (response) {
                        if (callback) {// вызов дополнительного события
                            callback.call(this, response.json.persistedObject);
                        } else { // вызов события по умолчанию
                            YAHOO.Bubbling.fire("nodeCreated", {
                                nodeRef: response.json.persistedObject,
                                bubblingLabel: this.options.bubblingLabel
                            });
                            YAHOO.Bubbling.fire("dataItemCreated", {
                                nodeRef: response.json.persistedObject,
                                bubblingLabel: this.options.bubblingLabel
                            });
                            Alfresco.util.PopupManager.displayMessage({
                                text: this.msg(successMessage ? successMessage : "message.save.success")
                            });
                        }
                        this.editDialogOpening = false;
                    },
                    scope: this
                },
                onFailure: {
                    fn: function (response) {
                        LogicECM.module.Base.Util.displayErrorMessageWithDetails(me.msg("logicecm.base.error"), me.msg("message.save.failure"), response.json.message);
                        me.editDialogOpening = false;
                        this.widgets.cancelButton.set("disabled", false);
                    },
                    scope: createDetails
                }
            }).show();
        },

        updateTemplateParams: function (params) {
            if (this.options.reportersFilterEnabled) {
                params.args = JSON.stringify({
                    allowedForPoint: this.getAllowedEmployees()
                });
            }
        }
    }, true);

})();