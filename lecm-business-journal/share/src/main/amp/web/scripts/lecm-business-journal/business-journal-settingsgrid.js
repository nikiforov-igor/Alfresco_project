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

(function() {

    LogicECM.module.BusinessJournal.SettingsGrid = function(containerId) {
        this.options.searchShowSecondary = false;
        //YAHOO.Bubbling.on("showSecondaryClicked", this.onShowSecondaryClicked, this);

        return LogicECM.module.BusinessJournal.DataGrid.superclass.constructor.call(this, containerId);
    };
    /**
     * Extend from LogicECM.module.Base.DataGrid
     */
    YAHOO.lang.extend(LogicECM.module.BusinessJournal.SettingsGrid, LogicECM.module.Base.DataGrid);
    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.BusinessJournal.SettingsGrid.prototype, {
        onSwitch: function DataGrid__onSwitch(turnOn)
        {
            var selectedItems = this.getSelectedItems();
            var url = Alfresco.constants.PROXY_URI + "lecm/business-journal/api/switch-logging";
            var me = this;
            Alfresco.util.Ajax.jsonPost(
                    {
                        url: url,
                        dataObj: {
                            items: selectedItems,
                            turnOn: turnOn
                        },
                        successCallback: {
                            fn: function(response) {
                                YAHOO.Bubbling.fire("datagridRefresh",
                                        {
                                            bubblingLabel: me.options.bubblingLabel
                                        });
                            },
                            scope: this
                        },
                        failureMessage: "Изменения не сохранены"
                    }
            );
        }
    }, true);
})();
