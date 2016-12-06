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
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-errands:limitation-date-days", false);
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-errands:limitation-date-type", false);
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-errands:limitation-date", true);
            } else if ("DATE" == value) {
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-errands:limitation-date-days", true);
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-errands:limitation-date-type", true);
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-errands:limitation-date", false);
            } else {
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-errands:limitation-date-days", true);
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-errands:limitation-date-type", true);
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-errands:limitation-date", true);
            }
        }
    }
})();
