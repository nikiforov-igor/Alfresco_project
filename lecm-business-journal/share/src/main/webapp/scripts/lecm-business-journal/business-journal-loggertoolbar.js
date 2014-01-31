/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
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
    LogicECM.module.BusinessJournal.LoggerToolbar = function (htmlId) {
        LogicECM.module.BusinessJournal.LoggerToolbar.superclass.constructor.call(this, "LogicECM.module.BusinessJournal.LoggerToolbar", htmlId);
        this.archivePanel = null;
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.BusinessJournal.LoggerToolbar, LogicECM.module.Base.Toolbar);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.BusinessJournal.LoggerToolbar.prototype,
        {
            _initButtons: function () {
                this.groupActions.turnOnButton = Alfresco.util.createYUIButton(this, "turn-on-logging", this.onTurnOn, {
                    disabled: true
                });
                this.groupActions.turnOffButton = Alfresco.util.createYUIButton(this, "turn-off-logging", this.onTurnOff, {
                    disabled: true
                });
            },

            archivePanel: null,

            // инициализация грида
            onInitDataGrid: function(layer, args) {
                var datagrid = args[1].datagrid;
                if ((!this.options.bubblingLabel || !datagrid.options.bubblingLabel) || this.options.bubblingLabel == datagrid.options.bubblingLabel){
                    this.modules.dataGrid = datagrid;
                    //this.archivePanel = new LogicECM.module.BusinessJournal.ArchivePanel("toolbar-archivePanel", datagrid);
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

            onTurnOn: function () {
                var dataGrid = this.modules.dataGrid;
	            if (dataGrid) {
		            dataGrid.onSwitch(true);
	            }
            },
            onTurnOff: function () {
                var dataGrid = this.modules.dataGrid;
	            if (dataGrid) {
		            dataGrid.onSwitch(false);
	            }
            }

        }, true);
})();

