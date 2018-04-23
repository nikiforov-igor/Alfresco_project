/**
 * Created by ABurlakov on 20.03.2017.
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

(function() {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.EdsTermsOfNotificationSettings = function(htmlId) {
        LogicECM.module.EdsTermsOfNotificationSettings.superclass.constructor.call(this, "LogicECM.module.EdsTermsOfNotificationSettings", htmlId, ["container", "json"]);

        YAHOO.Bubbling.on("beforeFormRuntimeInit", this.beforeFormInit, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.EdsTermsOfNotificationSettings, Alfresco.component.Base, {
        onReady: function () {
            this.loadSettings();
        },

        loadSettings: function() {
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "/lecm/eds/global-settings/api/getTermsOfNotificationSettings",
                successCallback: {
                    fn: function (response) {
                        if (response.json && response.json.nodeRef) {
                            this.loadForm(response.json.nodeRef);
                        }
                    },
                    scope: this
                },
                failureMessage: this.msg("message.failure")
            });
        },

        loadForm: function(settingsNode) {
            var htmlId = "eds-terms-of-notification-settings-edit-form-" + Alfresco.util.generateDomId();
            Alfresco.util.Ajax.request({
                url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                dataObj: {
                    htmlid: htmlId,
                    itemKind: "node",
                    itemId: settingsNode,
                    mode: "edit",
                    formUI: true,
                    submitType: "json",
                    showSubmitButton: "true",
                    showCaption: false
                },
                successCallback: {
                    scope: this,
                    fn: function (response) {
                        Dom.get(this.id + "-settings").innerHTML = response.serverResponse.responseText;
                        Dom.get(htmlId+ "-form-submit").value = this.msg("label.save");
                    }
                },
                failureMessage: "message.failure",
                execScripts: true
            });
        },

        beforeFormInit: function (layer, args) {
            YAHOO.Bubbling.unsubscribe("beforeFormRuntimeInit", this.beforeFormInit);
            var form = args[1].runtime;
            form.setSubmitAsJSON(true);
            form.setAJAXSubmit(true, {
                successCallback: {
                    scope: this,
                    fn: function () {
                        Alfresco.util.PopupManager.displayMessage({
                            text: Alfresco.util.message("message.save.success")
                        }
                    )}
                }
            });
        }
    });
})();