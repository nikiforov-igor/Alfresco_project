(function()
{
    if (YAHOO.Bubbling.addLayer("changeLimitationDateRadio")) {
        YAHOO.Bubbling.on("changeLimitationDateRadio", limitationDateRadioChanged);
    }
    YAHOO.Bubbling.on("createErrandsWFChangeLimitationDateRadio", limitationDateRadioChanged);

    function limitationDateRadioChanged(layer, args) {
        var value = args[1].value;
        var formId = args[1].formId;
        var dateField = "lecm-errands:limitation-date";
        var daysField = "lecm-errands:limitation-date-days";
        var daysTypeField = "lecm-errands:limitation-date-type";
        if (layer == "createErrandsWFChangeLimitationDateRadio") {
            dateField = "lecmErrandWf:limitationDate";
            daysField = "lecmErrandWf:limitationDateDays";
            daysTypeField = "lecmErrandWf:limitationDateType";
        }
        if (formId) {
            if ("DAYS" == value) {
                LogicECM.module.Base.Util.enableControl(formId, daysField);
                LogicECM.module.Base.Util.enableControl(formId, daysTypeField);
                LogicECM.module.Base.Util.disableControl(formId, dateField);
            } else if ("DATE" == value) {
                LogicECM.module.Base.Util.disableControl(formId, daysField);
                LogicECM.module.Base.Util.disableControl(formId, daysTypeField);
                LogicECM.module.Base.Util.enableControl(formId, dateField);
            } else {
                LogicECM.module.Base.Util.disableControl(formId, daysField);
                LogicECM.module.Base.Util.disableControl(formId, daysTypeField);
                LogicECM.module.Base.Util.disableControl(formId, dateField);
            }
        }
    }
})();
