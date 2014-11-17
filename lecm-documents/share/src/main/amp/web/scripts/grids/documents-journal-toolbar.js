if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.DocumentsJournal = LogicECM.module.DocumentsJournal || {};
(function () {
    LogicECM.module.DocumentsJournal.Toolbar = function (htmlId) {
        return LogicECM.module.DocumentsJournal.Toolbar.superclass.constructor.call(this, "LogicECM.module.DocumentsJournal.Toolbar", htmlId);
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.DocumentsJournal.Toolbar, LogicECM.module.Base.Toolbar);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.DocumentsJournal.Toolbar.prototype,
        {
            _initButtons: function () {
                this.toolbarButtons['defaultActive'].searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick);
                this.toolbarButtons['defaultActive'].exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick);
            }
        }, true);
})();