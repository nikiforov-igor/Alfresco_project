/**
 * Module Namespaces
 */
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.ReportsEditor = LogicECM.module.ReportsEditor|| {};


(function () {
    LogicECM.module.ReportsEditor.Toolbar = function (htmlId) {
        LogicECM.module.ReportsEditor.Toolbar.superclass.constructor.call(this, "LogicECM.module.ReportsEditor.Toolbar", htmlId);

        YAHOO.Bubbling.on("selectedItemsChanged", this.onCheckDocument, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.Toolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.ReportsEditor.Toolbar.prototype,
        {
            options: {
                bubblingLabel: null,
                createFormId: '',
                newRowDialogTitle: 'label.create-row.title',
                searchButtonsType: 'defaultActive',
                newRowButtonType: 'defaultActive'
            },
            doubleClickLock: false,

            _initButtons: function() {
                this.toolbarButtons[this.options.newRowButtonType].newDocumentButton = Alfresco.util.createYUIButton(this, 'newElementButton', this.onNewRow,
                    {
                        disabled: this.options.newRowButtonType != 'defaultActive',
                        value: 'create'
                    });

                this.toolbarButtons["defaultActive"].groupActionsButton = new YAHOO.widget.Button(
                        this.id + "-groupActionsButton",
                    {
                        type: "menu",
                        menu: [],
                        disabled: false
                    }
                );

                if (Dom.get(this.id + "-groupActionsButton") != null) {
                    this.toolbarButtons["defaultActive"].groupActionsButton.on("click", this.onCheckDocumentFinished.bind(this));
                    this.toolbarButtons["defaultActive"].groupActionsButton.getMenu().cfg.setProperty("classname", "group-actions-dialog");
                    this.toolbarButtons["defaultActive"].groupActionsButton.getMenu().subscribe("hide", this.clearOperationsList.bind(this));
                    this.toolbarButtons["defaultActive"].groupActionsButton.set("disabled", true);
                }

                this.toolbarButtons['defaultActive'].push(
                    Alfresco.util.createYUIButton(this, "importXmlButton", this.showImportDialog)
                );
                this.importFromSubmitButton = Alfresco.util.createYUIButton(this, "import-form-submit", this.onImportXML,{
                    disabled: true
                });
                Alfresco.util.createYUIButton(this, "import-form-cancel", this.hideImportDialog,{});
                YAHOO.util.Event.on(this.id + "-import-form-import-file", "change", this.checkImportFile, null, this);
                YAHOO.util.Event.on(this.id + "-import-error-form-show-more-link", "click", this.errorFormShowMore, null, this);
            },

            onNewRow: function () {
                var meta = this.modules.dataGrid.datagridMeta;
                if (meta != null && meta.nodeRef.indexOf(":") > 0) {
                    var destination = meta.nodeRef;
                    var itemType = meta.itemType;
                    this.showCreateDialog({itemType: itemType, nodeRef: destination, createFormId:this.options.createFormId}, null);
                }
            },

            showCreateDialog: function (meta, successMessage) {
                if (this.doubleClickLock) return;
                this.doubleClickLock = true;
                var me = this;
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    var addMsg = meta.addMessage;
                    var defaultMsg = this.msg("label.create-row.title");
                    var testMsg = this.msg(me.options.newRowDialogTitle);
                    if (testMsg != me.options.newRowDialogTitle){
                        defaultMsg = testMsg;
                    }
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-form-container_h", addMsg ? addMsg : defaultMsg ]
                    );

                    Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
                    me.doubleClickLock = false;
                };

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
                    {
                        itemKind: "type",
                        itemId: meta.itemType,
                        destination: meta.nodeRef,
                        mode: "create",
                        formId: meta.createFormId != null ? meta.createFormId : "",
                        submitType: "json"
                    });

                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
                createDetails.setOptions(
                    {
                        width: "60em",
                        templateUrl: templateUrl,
                        actionUrl: null,
                        destroyOnHide: true,
                        doBeforeDialogShow: {
                            fn: doBeforeDialogShow,
                            scope: this
                        },
                        onSuccess:{
                            fn: function DataGrid_onActionCreate_success(response) {
                                YAHOO.Bubbling.fire("dataItemCreated",
                                    {
                                        nodeRef: response.json.persistedObject,
                                        bubblingLabel: this.options.bubblingLabel
                                    });
                                if ("lecm-rpeditor:reportDescriptor" == meta.itemType || "lecm-rpeditor:subReportDescriptor" == meta.itemType){
                                    YAHOO.Bubbling.fire("newReportCreated",
                                        {
                                            reportId: response.json.persistedObject
                                        });
                                }
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg(successMessage ? successMessage : "message.save.success")
                                    });
                                this.doubleClickLock = false;
                            },
                            scope:this
                        },
                        onFailure: {
                            fn: function DataGrid_onActionCreate_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.failure")
                                    });
                                this.doubleClickLock = false;
                            },
                            scope: this
                        }
                    }).show();
            },

            onCheckDocumentFinished: function onCheckDocumentFinished_Function() {
                var button = this.toolbarButtons["defaultActive"].groupActionsButton;
                var menu = button.getMenu();
                var items = this.modules.dataGrid.getAllSelectedItems();
                var loadItem = [];
                loadItem.push({
                    text: "Загрузка...",
                    disabled: true
                });
                if (YAHOO.util.Dom.inDocument(menu.element)) {
                    menu.clearContent();
                    menu.addItems(loadItem);
                    menu.render();
                } else {
                    menu.itemData = loadItem;
                }
                var me = this;
                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI + "lecm/groupActions/list",
                    dataObj: {
                        items: JSON.stringify(items)
                    },
                    successCallback: {
                        fn: function (oResponse) {
                            var json = oResponse.json;
                            var actionItems = [];
                            var wideActionItems = [];
                            for (var i in json) {
                                if (!json[i].wide) {
                                    actionItems.push({
                                        text: json[i].title,
                                        value: json[i].id,
                                        onclick: {
                                            fn: me.onGroupActionsClick,
                                            obj: {
                                                actionId: json[i].id,
                                                type: json[i].type,
                                                withForm: json[i].withForm,
                                                items: items,
                                                workflowId: json[i].workflowId,
                                                label: json[i].title
                                            },
                                            scope: me
                                        }
                                    });
                                } else {
                                    wideActionItems.push({
                                        text: json[i].title,
                                        value: json[i].id,
                                        onclick: {
                                            fn: me.onGroupActionsClick,
                                            obj: {
                                                actionId: json[i].id,
                                                type: json[i].type,
                                                withForm: json[i].withForm,
                                                items: items,
                                                workflowId: json[i].workflowId,
                                                label: json[i].title
                                            },
                                            scope: me
                                        }
                                    });
                                }
                            }
                            if (actionItems.length == 0 && wideActionItems.length == 0) {
                                actionItems.push({
                                    text: this.msg("lecm.re.msg.no.operations"),
                                    disabled: true
                                });
                            }
                            if (actionItems.length != 0 && wideActionItems.length != 0) {
                                wideActionItems[0].classname = "toplineditem";
                            }
                            if (YAHOO.util.Dom.inDocument(menu.element)) {
                                menu.clearContent();
                                menu.addItems(actionItems);
                                menu.addItems(wideActionItems);
                                menu.render();
                            } else {
                                menu.addItems(actionItems);
                                menu.addItems(wideActionItems);
                            }
                        }
                    },
                    failureCallback: {
                        fn: function () {
                        }
                    },
                    scope: this,
                    execScripts: true
                });
            },
            clearOperationsList: function clearOperationsListFunction() {
                var button = this.toolbarButtons["defaultActive"].groupActionsButton;
                var menu = button.getMenu();
                if (YAHOO.util.Dom.inDocument(menu.element)) {
                    menu.clearContent();
                    menu.render();
                }
            },

            onCheckDocument: function () {
                var button = this.toolbarButtons["defaultActive"].groupActionsButton;

                var buttonName = this.msg("button.group-actions");
                var items = this.modules.dataGrid.getAllSelectedItems();
                if (items.length == 0) {
                    button.set("disabled", true);
                } else {
                    button.set("disabled", false);
                    buttonName += "<span class=\"group-actions-counter\">";
                    buttonName += "(" + items.length + ")";
                    buttonName += "</span>";
                }

                button.set("label", buttonName);
            },

            onGroupActionsClick: function onGroupActionsClick(p_sType, p_aArgs, p_oItem) {
                var items = p_oItem.items;
                if (p_oItem.type == "lecm-group-actions:script-action") {
                    Alfresco.util.PopupManager.displayPrompt({
                        title: this.msg("lecm.re.ttl.action.performing"),
                        text: this.msg("lecm.re.ttl.confirm.action") + " \"" + p_oItem.label + "\"",
                        buttons: [
                            {
                                text: this.msg("lecm.re.btn.ok"),
                                handler: function dlA_onAction_action() {
                                    this.destroy();
                                    Alfresco.util.Ajax.jsonPost({
                                        url: Alfresco.constants.PROXY_URI + "lecm/groupActions/exec",
                                        dataObj: {
                                            items: items,
                                            actionId: p_oItem.actionId
                                        },
                                        successCallback: {
                                            fn: function (oResponse) {
                                                this._actionResponse(p_oItem.actionId, oResponse, items);
                                            },
                                            scope: this
                                        },
                                        failureMessage: this.msg('message.failure'),
                                        execScripts: true
                                    });

                                }
                            },
                            {
                                text: this.msg("lecm.re.btn.cancel"),
                                handler: function dlA_onActionDelete_cancel() {
                                    this.destroy();
                                },
                                isDefault: true
                            }
                        ]
                    });
                }
            },
            _actionResponse: function actionResponseFunction(label, response, items) {
                var json = eval("(" + response.serverResponse.responseText + ")");
                if (json.forCollection) {
                    if (json.redirect != "") {
                        document.location.href = Alfresco.constants.URL_PAGECONTEXT + json.redirect;
                    } else if (json.openWindow) {
                        window.open(Alfresco.constants.URL_PAGECONTEXT + json.openWindow, "", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
                    } else if (json.withErrors) {
                        this._openMessageWindow(label, this.msg("lecm.re.action.error") + " \"" + label + "\"", false);
                    } else {
                        document.location.href = document.location.href;
                    }
                } else {
                    var message = "";
                    for (var i in json.items) {
                        var item = json.items[i];
                        var itemMessage = "";
                        var datagridItem = this.modules.dataGrid._findRecordByParameter(items[i], "nodeRef");
                        if (datagridItem != null) {
                            itemMessage = datagridItem.getData().itemData["prop_cm_name"].displayValue;
                        }

                        if (item.redirect != "") {
                            document.location.href = Alfresco.constants.URL_PAGECONTEXT + item.redirect;
                        } else if (item.openWindow) {
                            window.open(Alfresco.constants.URL_PAGECONTEXT + item.openWindow, "", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
                        } else {
                            message += "<div class=\"" + (item.withErrors ? "error-item" : "noerror-item") + "\">" + itemMessage + "</div>";
                        }
                    }
                    if (message != "") {
                        this._openMessageWindow(label, message, true);
                    }
                }
            },
            _openMessageWindow: function openMessageWindowFunction(title, message, reload) {
                Alfresco.util.PopupManager.displayPrompt(
                    {
                        title: this.msg("lecm.re.msg.operation.result") + " \"" + title + "\"",
                        text: message,
                        noEscape: true,
                        buttons: [
                            {
                                text: this.msg("lecm.re.btn.ok"),
                                handler: function dlA_onAction_action()
                                {
                                    this.destroy();
                                    if (reload) {
                                        document.location.href = document.location.href;
                                    }
                                }
                            }]
                    });
            }
        }, true);
})();