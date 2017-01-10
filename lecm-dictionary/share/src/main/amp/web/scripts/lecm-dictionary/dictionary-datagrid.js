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
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Dictionary
 */
LogicECM.module.Dictionary = LogicECM.module.Dictionary || {};

(function () {
    LogicECM.module.Dictionary.DataGrid = function (containerId, rootNode) {
        this.rootNode = rootNode;
        return LogicECM.module.Dictionary.DataGrid.superclass.constructor.call(this, containerId);
    };

    /**
     * Extend from LogicECM.module.Base.DataGrid
     */
    YAHOO.lang.extend(LogicECM.module.Dictionary.DataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Dictionary.DataGrid.prototype, {
        rootNode: null,

        isDeletable: function DataGrid_isDeletable(itemData) {
            return itemData["prop_deletable"] == undefined || itemData["prop_deletable"].value == "" || itemData["prop_deletable"].value == "true";
        },

        onDelete: function DataGridActions_onDelete(p_items, owner, actionsConfig, fnDeleteComplete, fnPrompt) {
            var me = this,
                items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];

            var itemNames = [];
            var propToShow = "prop_cm_name";
            for (var j = 0, jj = this.datagridColumns.length; j < jj; j++) {
                var column = this.datagridColumns[j];
                if (me.options.attributeForShow != null && column.name == me.options.attributeForShow) {
                    propToShow = column.formsName;
                    break;
                }
            }
            for (var k = 0; k < items.length; k++) {
                if (items[k] && items[k].itemData && items[k].itemData[propToShow]) {
                    itemNames.push("'" + items[k].itemData[propToShow].displayValue + "'");
                }
            }

            var itemsString = itemNames.join(", ");
            var fnActionDeleteConfirm = function DataGridActions__onActionDelete_confirm(items) {
                var nodeRefs = [];
                for (var i = 0, ii = items.length; i < ii; i++) {
                    nodeRefs.push(items[i].nodeRef);
                }
                var query = "";
                if (actionsConfig) {
                    var fullDelete = actionsConfig.fullDelete;
                    if (fullDelete != null) {
                        query = query + "full=" + fullDelete;
                    }
                    var trash = actionsConfig.trash;
                    if (fullDelete != null && trash != null) {
                        query = query + "&trash=" + trash;
                    }
                }
                this.modules.actions.genericAction(
                    {
                        success: {
                            callback: {
                                fn: function (response) {
                                    if(fnDeleteComplete){
                                        fnDeleteComplete.call(me);
                                    }
                                    if (response.json.overallSuccess){
                                        YAHOO.Bubbling.fire("dataItemsDeleted",
                                            {
                                                items:items,
                                                bubblingLabel:me.options.bubblingLabel
                                            });
                                        Alfresco.util.PopupManager.displayMessage(
                                            {
                                                text:me.msg((actionsConfig && actionsConfig.successMessage)? actionsConfig.successMessage : "message.delete.success", items.length)
                                            });
                                    } else {
                                        Alfresco.util.PopupManager.displayMessage(
                                            {
                                                text:me.msg("message.delete.failure")
                                            });
                                    }
                                }
                            }
                        },
                        failure: {
                            message: this.msg("message.delete.failure")
                        },
                        webscript: {
                            method: Alfresco.util.Ajax.DELETE,
                            name: "delete",
                            queryString: query
                        },
                        config: {
                            requestContentType: Alfresco.util.Ajax.JSON,
                            dataObj: {
                                nodeRefs: nodeRefs
                            }
                        }
                    });
            };

            if (!fnPrompt) {
                var nodeRefs = [];
                for (var i = 0, ii = items.length; i < ii; i++) {
                    nodeRefs.push(items[i].nodeRef);
                }
                Alfresco.util.Ajax.jsonPost(
                    {
                        url: Alfresco.constants.PROXY_URI + "lecm/dictionary/action/isDependents",
                        dataObj: {
                            nodeRefs: nodeRefs
                        },
                        successCallback: {
                            fn: function (response) {
                                var message = "";
                                if (response.json.isDependents) {
                                    message = this.msg("message.warning") + "\n";
                                }
                                message = (items.length > 1) ? message + this.msg("message.confirm.delete.group.description", items.length) : message + this.msg("message.confirm.delete.description", itemsString),
                                    fnPrompt = function onDelete_Prompt(fnAfterPrompt) {
                                        Alfresco.util.PopupManager.displayPrompt(
                                            {
                                                title: this.msg("message.confirm.delete.title", items.length),
                                                text: message,
                                                buttons: [
                                                    {
                                                        text: this.msg("button.delete"),
                                                        handler: function DataGridActions__onActionDelete_delete() {
                                                            this.destroy();
                                                            me.selectItems("selectNone");
                                                            fnAfterPrompt.call(me, items);
                                                        }
                                                    },
                                                    {
                                                        text: this.msg("button.cancel"),
                                                        handler: function DataGridActions__onActionDelete_cancel() {
                                                            this.destroy();
                                                        },
                                                        isDefault: true
                                                    }
                                                ]
                                            });
                                    }
                                fnPrompt.call(this, fnActionDeleteConfirm);
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function (response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.error.contains.links")
                                    });
                            },
                            scope: this
                        }
                    });
            }
        },

        onActionMove:function (item) {
            var me = this;

            var args = {};
            if (this.rootNode != null && this.rootNode.path != null) {
                args.rootLocation = this.rootNode.path;
            }

            new Alfresco.module.SimpleDialog("move-dictionary-item-form" + Alfresco.util.generateDomId()).setOptions({
                width: "50em",
                templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                templateRequestParams: {
                    submissionUrl: "/lecm/dictionary/action/changeParent/node",
                    itemKind: "type",
                    itemId: "lecm-dic:hierarchical_dictionary_values",
                    formId: "moveDictionaryItem",
                    mode: "create",
                    submitType: "json",
                    showCancelButton: true,
                    itemNodeRef: item.nodeRef,
                    ignoreNodes: item.nodeRef,
                    args: JSON.stringify(args),
					showCaption: false
                },
                actionUrl: null,
                destroyOnHide: true,
                doBeforeDialogShow: {
                    fn: function (p_form, p_dialog) {
                        var contId = p_dialog.id + "-form-container";
                        var dialogName = me.msg("title.dictionary.move");
                        Alfresco.util.populateHTML(
                            [contId + "_h", dialogName]
                        );

                        p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                    }
                },
                onSuccess: {
                    fn: function (response) {
                        YAHOO.Bubbling.fire("selectedCurrentNode");
                    },
                    scope: this
                }
            }).show();
        }
    }, true);
})();
