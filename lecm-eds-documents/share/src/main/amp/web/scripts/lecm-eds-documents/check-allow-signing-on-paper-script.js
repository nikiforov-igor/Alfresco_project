(function () {
    var Bubbling = YAHOO.Bubbling;

    Bubbling.on('allowSigningOnPaperScript', checkAllowSigningOnPaperScript);

    function checkAllowSigningOnPaperScript(layer, args) {
        var formId = args[1].formId;

        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + "lecm/eds/global-settings/api/getSettingsNode",
            successCallback: {
                fn: function (response) {
                    if (response && response.json.nodeRef) {
                        var isAllowSigningOnPaper = response.json.isAllowSigningOnPaper;
                        if (isAllowSigningOnPaper) {
                            LogicECM.module.Base.Util.showControl(formId, "lecm-signing-v2-aspects:signed-on-paper");
                            LogicECM.module.Base.Util.showControl(formId, "lecm-signing-v2-aspects:signerEmployeeAssoc");
                            LogicECM.module.Base.Util.showControl(formId, "lecm-signing-v2-aspects:signingDate");
                        } else {
                            LogicECM.module.Base.Util.hideControl(formId, "lecm-signing-v2-aspects:signed-on-paper");
                            LogicECM.module.Base.Util.hideControl(formId, "lecm-signing-v2-aspects:signerEmployeeAssoc");
                            LogicECM.module.Base.Util.hideControl(formId, "lecm-signing-v2-aspects:signingDate");
                        }
                    }
                }
            }
        })
    }
})();