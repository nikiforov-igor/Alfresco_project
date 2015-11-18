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
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Orgstructure module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.BusinessJournal
 */
LogicECM.module.BusinessJournal = LogicECM.module.BusinessJournal || {};

/**
 * Displays a list of Toolbar
 *
 * @namespace LogicECM
 * @class LogicECM.module.BusinessJournal.Toolbar
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom;

    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {LogicECM.module.BusinessJournal.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.BusinessJournal.Toolbar = function (htmlId) {
        LogicECM.module.BusinessJournal.Toolbar.superclass.constructor.call(this, "LogicECM.module.BusinessJournal.Toolbar", htmlId);
        this.archivePanel = null;
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.BusinessJournal.Toolbar, LogicECM.module.Base.Toolbar);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.BusinessJournal.Toolbar.prototype,
        {
            _initButtons: function () {
                this.toolbarButtons["defaultActive"].searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick);

                this.toolbarButtons["defaultActive"].exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick);

                this.toolbarButtons["defaultActive"].deleteButton = Alfresco.util.createYUIButton(this, "archiveByDateButton", this.onArchiveRowsDialog);

                this.groupActions.exportCsvButton = Alfresco.util.createYUIButton(this, "exportCsvButton", this.onExportCSV, {
                    disabled: true
                });
            },

            archivePanel: null,

            // инициализация грида
            onInitDataGrid: function(layer, args) {
                var datagrid = args[1].datagrid;
                if ((!this.options.bubblingLabel || !datagrid.options.bubblingLabel) || this.options.bubblingLabel == datagrid.options.bubblingLabel){
                    this.modules.dataGrid = datagrid;
                    this.archivePanel = new LogicECM.module.BusinessJournal.ArchivePanel("toolbar-archivePanel", datagrid);
                }
            },

            onArchiveRowsDialog: function Toolbar_onDeleteRow() {
                if (this.modules.dataGrid.totalRecords == 0) {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text: this.msg("message.alert.no-elements")
                        });
                }

                var items = this.modules.dataGrid.getSelectedItems();
                if (items.length == 0) {
                    if (this.archivePanel && this.archivePanel.panel) {
                        Dom.setStyle(this.archivePanel.id, "display", "block");
                        this.archivePanel.panel.show();
                    } else {
                        alert("Не удалось найти панель!");
                    }
                } else {
                    var dataGrid = this.modules.dataGrid;
                    if (dataGrid) {
                        // Get the function related to the clicked item
                        var fn = "onActionDelete";
                        if (fn && (typeof dataGrid[fn] == "function")) {
                            dataGrid[fn].call(dataGrid, dataGrid.getSelectedItems());
                        }
                    }
                }
            },

            onSelectedItemsChanged: function Toolbar_onSelectedItemsChanged(layer, args) {
                if (this.modules.dataGrid) {
                    var items = this.modules.dataGrid.getSelectedItems();
                    for (var index in this.groupActions) {
                        if (this.groupActions.hasOwnProperty(index)) {
                            var action = this.groupActions[index];
                            action.set("disabled", (items.length === 0));
                        }
                    }
                }
            },

            onExportCSV: function () {
                var dataGrid = this.modules.dataGrid;
	            if (dataGrid) {
		            dataGrid.onExportCsv("business-journal");
	            }
            }

        }, true);
})();

(function () {
    var $html = Alfresco.util.encodeHTML;

    LogicECM.module.BusinessJournal.ArchivePanel = function (id, datagrid) {
        LogicECM.module.BusinessJournal.ArchivePanel.superclass.constructor.call(this, "LogicECM.module.BusinessJournal.ArchivePanel", id, ["button", "container", "json"]);
        this.panel = null;
        this.panelButtons = {};
        this.dataGrid = datagrid;

        YAHOO.Bubbling.on("hidePanel", this.onCancel, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.BusinessJournal.ArchivePanel, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.module.BusinessJournal.ArchivePanel.prototype,
        {
            panel: null,
            dataGrid: null,
            panelButtons: null,
            isReady: false,

            onReady: function () {
                this.panel = Alfresco.util.createYUIPanel(this.id,
                    {
                        width: "500px"
                    });
                this.panelButtons.archiveButton = Alfresco.util.createYUIButton(this, "archiveButton", this.onArchive, {});
                this.panelButtons.cancelButton = Alfresco.util.createYUIButton(this, "cancelButton", this.onCancel, {});
            },

            onArchive: function () {
                var dateValue = Dom.get("archiveDate").value;
                if (this.isValidDateArchiveTo(dateValue)) {
                    var timerShowLoadingMessage = null;
                    var loadingMessage = null;
                    var me = this;

                    var fnShowLoadingMessage = function nShowLoadingMessage() {
                        if (timerShowLoadingMessage) {
                            loadingMessage = Alfresco.util.PopupManager.displayMessage(
                                {
                                    displayTime: 0,
                                    text: '<span class="wait">' + $html(this.msg("label.loading")) + '</span>',
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
                                nodeRefs: [],
                                dateArchiveTo: Alfresco.util.formatDate(Alfresco.util.fromISO8601(dateValue), "yyyy-mm-dd")
                            },
                            successCallback: {
                                fn: function (response) {
                                    destroyLoaderMessage();
                                    this.onCancel();
                                    YAHOO.Bubbling.fire("dataItemsDeleted", {
                                        items: response.json.results,
                                        bubblingLabel: this.options.bubblingLabel
                                    });
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
                } else {
                    var message = this.dataGrid.msg("message.alert.incorrect-date-format");
                    Alfresco.util.PopupManager.displayMessage({
                        text: message
                    }, Dom.get("toolbar-archivePanel"));
                }
            },

            isValidDateArchiveTo: function (dateArchiveTo) {
                return (dateArchiveTo.length > 0) && (!Dom.hasClass("archiveDate-cntrl-date", "invalid"));
            },

            onCancel: function (layer, args) {
                if (this.panel != null) {
                    this.panel.hide();
                    Dom.setStyle(this.id, "display", "none");
                }
            }
        }, true);
})();
