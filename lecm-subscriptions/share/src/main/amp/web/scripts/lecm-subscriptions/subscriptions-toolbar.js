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
 * LogicECM Subscriptions module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Subscriptions.Subscriptions
 */
LogicECM.module.Subscriptions = LogicECM.module.Subscriptions || {};

/**
 * Displays a list of Toolbar
 *
 * @namespace LogicECM
 * @class LogicECM.module.Subscriptions.Toolbar
 */
(function () {
    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {LogicECM.module.Subscriptions.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.Subscriptions.Toolbar = function (htmlId) {
        return LogicECM.module.Subscriptions.Toolbar.superclass.constructor.call(this, "LogicECM.module.Subscriptions.Toolbar", htmlId);
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.Subscriptions.Toolbar, LogicECM.module.Base.Toolbar);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Subscriptions.Toolbar.prototype,
        {
            _initButtons: function () {
                this.toolbarButtons["defaultActive"].newRowButton = Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow,
                    {
                        value: "create"
                    });
                this.toolbarButtons["defaultActive"].searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick);
                this.toolbarButtons["defaultActive"].exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick);

                this.groupActions.deleteButton = Alfresco.util.createYUIButton(this, "deleteButton", this.onDeleteRow,
                    {
                        disabled: true
                    });
            }
        }, true);
})();