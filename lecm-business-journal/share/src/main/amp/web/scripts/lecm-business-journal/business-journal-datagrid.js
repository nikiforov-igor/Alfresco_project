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
 * LogicECM BusinessJournal module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.BusinessJournal
 */
LogicECM.module.BusinessJournal = LogicECM.module.BusinessJournal || {};

(function () {

    LogicECM.module.BusinessJournal.DataGrid = function (containerId) {
        this.options.searchShowSecondary = false;

        YAHOO.Bubbling.on("showSecondaryClicked", this.onShowSecondaryClicked, this);

        return LogicECM.module.BusinessJournal.DataGrid.superclass.constructor.call(this, containerId);
    };

    /**
     * Extend from LogicECM.module.Base.DataGrid
     */
    YAHOO.lang.extend(LogicECM.module.BusinessJournal.DataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.BusinessJournal.DataGrid.prototype, {
        onShowSecondaryClicked: function (layer, args) {
            var cbShowSecondary = YAHOO.util.Dom.get(this.id + "-cbShowSecondary");
            if (!cbShowSecondary) {
                return;
            }
            this.options.searchShowSecondary = cbShowSecondary.checked;

            if (!this.datagridMeta.searchConfig) {
                this.datagridMeta.searchConfig = {};
            }
            this.datagridMeta.searchConfig.showSecondary = this.options.searchShowSecondary;
            var obj = {
                datagridMeta: this.datagridMeta
            };

            YAHOO.Bubbling.fire("activeGridChanged", obj);
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
                        oData = YAHOO.lang.isArray(oData) ? oData : [oData];
                        for (var i = 0, ii = oData.length, data; i < ii; i++) {
                            data = oData[i];

                            var columnContent = "";
                            switch (datalistColumn.dataType.toLowerCase()) { //  меняем отрисовку для конкретных типов
                                /*case "lecm-busjournal:eventcategory":
                                    columnContent += grid.getDicValueView(data.value, data.displayValue);
                                    break;

                                case "lecm-busjournal:objecttype":
                                    columnContent += grid.getDicValueView(data.value, data.displayValue);
                                    break;
*/
                                case "datetime":
                                    columnContent += '<span class="datagrid-datetime">' + Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), "dd.mm.yyyy HH:MM:ss") + '</span>';
                                    break;
                                case "lecm-orgstr:employee":
                                    columnContent += grid.getEmployeeView(data.value, data.displayValue);
                                    break;
                                default:
                                    break;
                            }
                            switch (datalistColumn.name.toLowerCase()) { //  меняем отрисовку для конкретных колонок
                                case "lecm-busjournal:bjrecord-mainobject-assoc":
                                    columnContent += grid.getDicValueView(data.value, data.displayValue);
                                    break;
                                case "lecm-busjournal:secondary-objects":
                                    // если нет доп объектов (строка '; ; ; ; ') - выводим пустую строку
                                    if (data.displayValue && data.displayValue.length <= 8) {
                                        columnContent += "(нет)";
                                    } else {
                                        columnContent = data.displayValue;
                                    }
                                    break;
                                case "lecm-busjournal:bjrecord-description":
                                    columnContent += data.displayValue;
                                    break;
                                default:
                                    break;
                            }
                            if (columnContent != "") {
                                if (grid.options.attributeForShow != null && datalistColumn.name == grid.options.attributeForShow) {
                                    html += "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({" +
                                        "itemKind: \'type\'," +
                                        "itemId: \'lecm-busjournal:bjRecord\'," +
                                        "htmlid: \'bj-view-node-form-htmlid\'," +
                                        "nodeId:\'" + oRecord.getData("nodeRef") +
                                        "\'})\">" + columnContent + "</a>";
                                } else {
                                    html += columnContent;
                                }

                                if (i < ii - 1) {
                                    html += "<br />";
                                }
                            }
                        }
                    }
                }
            }
            return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
        },
        getDicValueView: function DataGrid_getDicitonaryValueView(nodeRef, displayValue) {
            var result = "";
	        if (displayValue.length > 0) {
		        result = "<span>";
	            if (nodeRef != null && nodeRef.length > 0) {
                    result += "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + nodeRef + "\'})\">" + displayValue + "</a>";
		        } else {
		            result += displayValue;
	            }
		        result += "</span>";
	        }
            return result;
        },
        getEmployeeView: function DataGrid_getEmployeeView(employeeNodeRef, displayValue) {
            if (displayValue.length == 0 || employeeNodeRef == null || employeeNodeRef.indexOf("://") < 0) {
                return "Система";
            }
            return "<span class='person'><a href='javascript:void(0);'" + "onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + employeeNodeRef + "\',title:\'logicecm.employee.view\'})\">" + displayValue + "</a></span>";
        },

        onExportCsv: function DataGrid__onExportCsv(fileName)
        {
            var selectedItems = this.getSelectedItems();

	        var url =  Alfresco.constants.PROXY_URI + "lecm/business-journal/api/export-records" +
		        "?timeZoneOffset=" + encodeURIComponent(new Date().getTimezoneOffset());
            for (var i = 0; i < selectedItems.length;i++) {
	            url += "&id=" + encodeURIComponent(selectedItems[i].nodeRef);
            }

	        window.open(url, "_self");
        },

        onActionDelete: function DataGridActions_onActionDelete(p_items, owner, actionsConfig, fnDeleteComplete) {
            var timerShowLoadingMessage = null;
            var loadingMessage = null;
            var me = this,
                items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];

            var itemNames = [];
            for (var k = 0; k < items.length; k++) {
                if (items[k] && items[k].itemData && items[k].itemData["lecm-busjournal:bjRecord-description"]) {
                    itemNames.push("'" + items[k].itemData["lecm-busjournal:bjRecord-description"].displayValue + "'");
                }
            }

            var itemsString = itemNames.join(", ");
            var fnActionDeleteConfirm = function DataGridActions__onActionDelete_confirm(items) {
                var nodeRefs = [];
                for (var i = 0, ii = items.length; i < ii; i++) {
                    nodeRefs.push(items[i].nodeRef);
                }
                var fnShowLoadingMessage = function nShowLoadingMessage() {
                    if (timerShowLoadingMessage) {
                        loadingMessage = Alfresco.util.PopupManager.displayMessage(
                            {
                                displayTime: 0,
                                text: '<span class="wait">' + Alfresco.util.encodeHTML(this.msg("label.loading")) + '</span>',
                                noEscape: true
                            });

                        if (YAHOO.env.ua.ie > 0) {
                            this.loadingMessageShowing = true;
                        }
                        else {
                            loadingMessage.showEvent.subscribe(function () {
                                this.loadingMessageShowing = true;
                            }, this, true);
                        }
                    }
                };

                // Slow data webscript message
                this.loadingMessageShowing = false;
                timerShowLoadingMessage = YAHOO.lang.later(500, this, fnShowLoadingMessage);

                var destroyLoaderMessage = function DataGrid__uDG_destroyLoaderMessage() {
                    if (timerShowLoadingMessage) {
                        // Stop the "slow loading" timed function
                        timerShowLoadingMessage.cancel();
                        timerShowLoadingMessage = null;
                    }
                    if (loadingMessage) {
                        if (this.loadingMessageShowing) {
                            // Safe to destroy
                            loadingMessage.destroyWithAnimationsStop();
                            loadingMessage = null;
                        }
                        else {
                            // Wait and try again later. Scope doesn't get set correctly with "this"
                            YAHOO.lang.later(100, me, destroyLoaderMessage);
                        }
                    }
                };

                var sUrl = Alfresco.constants.PROXY_URI + "lecm/business-journal/api/record/archive";
                Alfresco.util.Ajax.jsonPost(
                    {
                        url: sUrl,
                        dataObj: {
                            nodeRefs: nodeRefs
                        },
                        successCallback: {
                            fn: function (response) {
                                destroyLoaderMessage();
                                if (response.json){
                                    if (response.json.overallSuccess){
                                        if(!this.options.searchShowInactive){
                                            YAHOO.Bubbling.fire("dataItemsDeleted", {
                                                items: response.json.results,
                                                bubblingLabel: this.options.bubblingLabel
                                            });
                                        } else {
                                            var obj = {
                                                datagridMeta: this.datagridMeta
                                            };
                                            YAHOO.Bubbling.fire("activeGridChanged", obj);
                                        }
                                    } else {
                                        Alfresco.util.PopupManager.displayMessage(
                                            {
                                                text:this.msg("message.delete.failure")
                                            });
                                    }
                                } else {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text:this.msg("message.delete.failure")
                                        });
                                }
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function (response) {
                                destroyLoaderMessage();
                                alert("Failed to load webscript")
                            },
                            scope: this
                        }
                    });
            };

            var fnPrompt = function onDelete_Prompt(fnAfterPrompt) {
                Alfresco.util.PopupManager.displayPrompt(
                    {
                        title: this.msg("message.confirm.delete.title", items.length),
                        text: (items.length > 1) ? this.msg("message.confirm.delete.group.description", items.length) : this.msg("message.confirm.delete.description", itemsString),
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

		_setupPaginatior: function DataGrid_setupPaginatior() {
            if (this.options.usePagination) {
                var handlePagination = function DataGrid_handlePagination(state, me) {
                    me.widgets.paginator.setState(state);
                };

                this.widgets.paginator = new YAHOO.widget.Paginator(
                    {
                        containers: [this.id + "-paginatorBottom"],
                        rowsPerPage: this.options.pageSize,
                        initialPage: this.options.initialPage,
                        template: this.msg("pagination.template"),
                        pageReportTemplate: this.msg("pagination.template.page-report"),
                        previousPageLinkLabel: this.msg("pagination.previousPageLinkLabel"),
                        nextPageLinkLabel: this.msg("pagination.nextPageLinkLabel")
                    });

                this.widgets.paginator.setAttributeConfig('pageReportValueGenerator', {
                    value : function (paginator) {
                        var curPage = paginator.getCurrentPage(),
                            records = paginator.getPageRecords();

                        return {
                            'currentPage' : records ? curPage : 0,
                            'totalPages'  : paginator.getTotalPages(),
                            'startIndex'  : records ? records[0] : 0,
                            'endIndex'    : records ? records[1] : 0,
                            'startRecord' : records ? records[0] + 1 : 0,
                            'endRecord'   : records ? records[1] + 1 : 0,
                            'totalRecords': paginator.get('totalRecords') == -1 ? 'Неизвестно' : paginator.get('totalRecords')
                        };
                    },
                    validator : YAHOO.lang.isFunction
                });

                this.widgets.paginator.subscribe("changeRequest" + this.id, handlePagination, this);

                // Display the bottom paginator bar
                Dom.setStyle(this.id + "-datagridBarBottom", "display", "none");
            }
        }
    }, true);
})();
