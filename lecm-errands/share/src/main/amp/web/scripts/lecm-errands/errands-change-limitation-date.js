(function()
{
    if (YAHOO.Bubbling.addLayer("changeLimitationDateRadio")) {
        YAHOO.Bubbling.on("changeLimitationDateRadio", limitationDateRadioChanged);
    }

    function limitationDateRadioChanged(layer, args) {
        var value = args[1].value;
        var formId = args[1].formId;

        if (formId) {
            if ("DAYS" == value) {
                LogicECM.module.Base.Util.enableControl(formId, "lecm-errands:limitation-date-days");
                LogicECM.module.Base.Util.enableControl(formId, "lecm-errands:limitation-date-type");
                LogicECM.module.Base.Util.disableControl(formId, "lecm-errands:limitation-date");
            } else if ("DATE" == value) {
                LogicECM.module.Base.Util.disableControl(formId, "lecm-errands:limitation-date-days");
                LogicECM.module.Base.Util.disableControl(formId, "lecm-errands:limitation-date-type");
                LogicECM.module.Base.Util.enableControl(formId, "lecm-errands:limitation-date");
            } else {
                LogicECM.module.Base.Util.disableControl(formId, "lecm-errands:limitation-date-days");
                LogicECM.module.Base.Util.disableControl(formId, "lecm-errands:limitation-date-type");
                LogicECM.module.Base.Util.disableControl(formId, "lecm-errands:limitation-date");
            }
        }
    }
})();
