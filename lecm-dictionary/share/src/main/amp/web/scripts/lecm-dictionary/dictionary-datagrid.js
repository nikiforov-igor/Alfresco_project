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
 * @class LogicECM.module.Dictionary
 */
LogicECM.module.Dictionary = LogicECM.module.Dictionary || {};

(function () {
    var $html = Alfresco.util.encodeHTML,
        $links = Alfresco.util.activateLinks,
        $userProfile = Alfresco.util.userProfileLink;
    var attributeForShow;

    LogicECM.module.Dictionary.DataGrid = function (containerId, attributeForShowing) {
        attributeForShow = attributeForShowing;
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
        isDeletable: function DataGrid_isDeletable(itemData) {
            return itemData["prop_deletable"] == undefined || itemData["prop_deletable"].value == "" || itemData["prop_deletable"].value == "true";
        },

        getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {
            var html = "";

            // Populate potentially missing parameters
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
                        var content;
                        oData = YAHOO.lang.isArray(oData) ? oData : [oData];

                        for (var i = 0, ii = oData.length, data; i < ii; i++) {
                            data = oData[i];

                            switch (datalistColumn.dataType.toLowerCase()) {
                                case "cm:person":
                                    html += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
                                    break;

                                case "datetime":
                                    content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), grid.msg("date-format.default"));
                                    if (datalistColumn.name == attributeForShow) {
                                        content = "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
                                    }
                                    html += content;
                                    break;

                                case "date":
                                    content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), grid.msg("date-format.defaultDateOnly"));
                                    if (datalistColumn.name == attributeForShow) {
                                        content = "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
                                    }
                                    html += content;
                                    break;

                                case "text":
                                    var hexColorPattern = /^#[0-9a-f]{6}$/i;
                                    if (hexColorPattern.test(data.displayValue)) {
                                        content = data.displayValue + '<div class="color-block" style="background-color: ' + data.displayValue + ';">&nbsp</div>';
                                    } else {
                                        content = $html(data.displayValue);
                                    }

                                    if (datalistColumn.name == attributeForShow) {
                                        html += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
                                    } else {
                                        html += $links(content);
                                    }
                                    break;

                                case "boolean":
                                    if (data.value) {
                                        html += '<div class="centered">';
                                        html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                        html += '</div>';
                                    }
                                    break;

                                default:
                                    if (datalistColumn.type == "association") {
                                        html += $html(data.displayValue);
                                    } else {
                                        if (data.displayValue != "false" && data.displayValue != "true") {
                                            html += $html(data.displayValue);
                                        } else {
                                            if (data.displayValue == "true") {
                                                html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                            }
                                        }
                                    }
                                    break;
                            }

                            if (i < ii - 1) {
                                html += "<br />";
                            }
                        }
                    }
                }
            }

            return html.length > 0 ? html : null;  // возвращаем NULL чтобы вызвался основной метод отрисовки
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
        }
    }, true);
})();
