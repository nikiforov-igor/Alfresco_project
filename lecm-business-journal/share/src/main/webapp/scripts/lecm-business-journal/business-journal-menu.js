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
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.BusinessJournal
 */
LogicECM.module.BusinessJournal = LogicECM.module.BusinessJournal || {};

/**
 * BusinessJournal module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.BusinessJournal
 */
(function () {

    LogicECM.module.BusinessJournal.Menu = function (htmlId) {
        return LogicECM.module.BusinessJournal.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.BusinessJournal.Menu",
            htmlId,
            ["button"]);
    };

    YAHOO.extend(LogicECM.module.BusinessJournal.Menu, Alfresco.component.Base, {
        onReady: function () {
            var onRecordsClick = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "business-journal";
            };
            this.widgets.recordsButton = Alfresco.util.createYUIButton(this, "bj-summaryBtn", onRecordsClick, {
                disabled: !LogicECM.module.BusinessJournal.IS_ENGINEER
            });

            var onArchiverSettingsClick = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "business-journal-archiver-settings";
            };
            this.widgets.recordsButton = Alfresco.util.createYUIButton(this, "bj-archiver-settingsBtn", onArchiverSettingsClick, {
                disabled: !LogicECM.module.BusinessJournal.IS_ENGINEER
            });
            var onLoggerSettingsClick = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "business-journal-logger-settings";
            };
            this.widgets.recordsButton = Alfresco.util.createYUIButton(this, "bj-logger-settingsBtn", onLoggerSettingsClick, {
                disabled: !LogicECM.module.BusinessJournal.IS_ENGINEER
            });
        }
    });
})();
