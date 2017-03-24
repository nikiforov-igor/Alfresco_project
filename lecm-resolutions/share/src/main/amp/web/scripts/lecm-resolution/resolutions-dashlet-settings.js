/**
 * Created by ABurlakov on 24.03.2017.
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

    LogicECM.module.ResolutionsDashletSettings = function(htmlId) {
        LogicECM.module.ResolutionsDashletSettings.superclass.constructor.call(this, "LogicECM.module.ResolutionsDashletSettings", htmlId, ["container", "json"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.ResolutionsDashletSettings, Alfresco.component.Base, {
        onReady: function () {
            this.loadSettings();
        },

        loadSettings: function() {
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/resolutions/dashlet/settings",
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
            var me = this;
            var htmlId = "resolutions-dashlet-settings-edit-form-" + Alfresco.util.generateDomId();
            Alfresco.util.Ajax.request(
                {
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                    dataObj: {
                        htmlid: htmlId,
                        itemKind:"node",
                        itemId: settingsNode,
                        mode: "edit",
                        formUI: true,
                        submitType:"json",
                        showSubmitButton:"true",
                        showCaption: false
                    },
                    successCallback: {
                        fn: function (response) {
                            var container = Dom.get(me.id + "-settings");
                            container.innerHTML = response.serverResponse.responseText;

                            Dom.get(htmlId+ "-form-submit").value = me.msg("label.save");

                            var form = new Alfresco.forms.Form(htmlId + "-form");
                            form.setSubmitAsJSON(true);
                            form.setAJAXSubmit(true,
                                {
                                    successCallback:
                                    {
                                        fn: function () {
                                            Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text: Alfresco.util.message("message.save.success")
                                                }
                                            )},
                                        scope: this
                                    }
                                });
                            form.init();
                        }
                    },
                    failureMessage: "message.failure",
                    execScripts: true
                });
        }
    });
})();