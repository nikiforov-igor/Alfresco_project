(function()
{
    if (YAHOO.Bubbling.addLayer("changeResolutionLimitationDateRadio")) {
        YAHOO.Bubbling.on("changeResolutionLimitationDateRadio", limitationDateRadioChanged);
    }

    function limitationDateRadioChanged(layer, args) {
        var value = args[1].value;
        var formId = args[1].formId;

        if (formId) {
            if ("DAYS" == value) {
                LogicECM.module.Base.Util.enableControl(formId, "lecm-resolutions:limitation-date-days");
                LogicECM.module.Base.Util.enableControl(formId, "lecm-resolutions:limitation-date-type");
                LogicECM.module.Base.Util.disableControl(formId, "lecm-resolutions:limitation-date");
            } else if ("DATE" == value) {
                LogicECM.module.Base.Util.disableControl(formId, "lecm-resolutions:limitation-date-days");
                LogicECM.module.Base.Util.disableControl(formId, "lecm-resolutions:limitation-date-type");
                LogicECM.module.Base.Util.enableControl(formId, "lecm-resolutions:limitation-date");
            } else {
                LogicECM.module.Base.Util.disableControl(formId, "lecm-resolutions:limitation-date-days");
                LogicECM.module.Base.Util.disableControl(formId, "lecm-resolutions:limitation-date-type");
                LogicECM.module.Base.Util.disableControl(formId, "lecm-resolutions:limitation-date");
            }
        }
    }
})();
