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

(function()
{
    var Dom = YAHOO.util.Dom;

    LogicECM.module.EDSTermOfExecuteSettings = function(htmlId)
    {
        LogicECM.module.EDSTermOfExecuteSettings.superclass.constructor.call(this, "LogicECM.module.EDSTermOfExecuteSettings", htmlId, ["container", "json"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.EDSTermOfExecuteSettings, Alfresco.component.Base,
        {
            onReady: function () {
                this.loadSettings();
            },

            loadSettings: function() {
                Alfresco.util.Ajax.jsonGet({
                    url: Alfresco.constants.PROXY_URI + "lecm/documents/global-settings/api/getSettingsNode",
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
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                        dataObj: {
                            htmlid: "eds-term-of-execute-settings-edit-form",
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

                                Dom.get("eds-term-of-execute-settings-edit-form-submit").value = me.msg("label.save");

                                var form = new Alfresco.forms.Form("eds-term-of-execute-settings-edit-form-form");
                                form.setSubmitAsJSON(true);
                                form.setAJAXSubmit(true,
                                    {
                                        successCallback:
                                        {
                                            fn: me.onSuccess,
                                            scope: this
                                        }
                                    });
                                form.init();
                            }
                        },
                        failureMessage: "message.failure",
                        execScripts: true
                    });
            },

            onSuccess: function (response)
            {
                if (response && response.json) {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text: Alfresco.util.message("message.save.success")
                        });
                } else {
                    Alfresco.util.PopupManager.displayPrompt(
                        {
                            text: Alfresco.util.message("message.failure")
                        });
                }
            }
        });
})();