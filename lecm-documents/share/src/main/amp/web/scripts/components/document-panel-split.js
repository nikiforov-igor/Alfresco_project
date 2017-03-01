/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
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
LogicECM.services = LogicECM.services || {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.DocumentPanelSplitView = function (fieldHtmlId) {
        LogicECM.module.DocumentPanelSplitView.superclass.constructor.call(this, "LogicECM.module.DocumentPanelSplitView", fieldHtmlId, []);

        this.controlId = fieldHtmlId + "-cntrl";
        this.services.docViewPreferences = LogicECM.services.DocumentViewPreferences;
        YAHOO.Bubbling.on("panelSplitedChanged", this.onPanelSplitedChanged, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.DocumentPanelSplitView, Alfresco.component.Base,
        {
            options: {
                documentNodeRef: null
            },

            controlId: null,
            splitPanelButton: null,

            onReady: function () {
                this.splitPanelButton = Alfresco.util.createYUIButton(this, this.controlId + "-split-panel-button", this.onSwitchPanelSplit, {},
                    Dom.get(this.controlId + "-split-panel-button"));
                this.render();
            },

            render: function () {
                if (this.getPanelSplited()) {
                    Dom.get(this.controlId + "-split-panel-button").title = this.msg("button.split-panel.disable");
                    Dom.addClass(this.controlId + "-split-panel", "enabled");
                } else {
                    Dom.get(this.controlId + "-split-panel-button").title = this.msg("button.split-panel.enable");
                    Dom.removeClass(this.controlId + "-split-panel", "enabled");
                }
            },

            onPanelSplitedChanged: function () {
                this.render();
            },

            onSwitchPanelSplit: function () {
                var isPanelSplit = !this.getPanelSplited();
                this.setPanelSplited(isPanelSplit);
                this.render();
            },

            setPanelSplited: function (isPanelSplit) {
                this.services.docViewPreferences.setPanelSplited(isPanelSplit);
            },

            getPanelSplited: function () {
                return this.services.docViewPreferences.getPanelSplited();
            }
        });
})();