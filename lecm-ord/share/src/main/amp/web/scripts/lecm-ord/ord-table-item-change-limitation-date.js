(function()
{
    YAHOO.Bubbling.on("changeLimitationDateRadio", limitationDateRadioChanged);

    function limitationDateRadioChanged(layer, args) {
        var value = args[1].value;
        var formId = args[1].formId;
        var dateField = "lecm-ord-table-structure:item-limitation-date";
        var daysField = "lecm-ord-table-structure:item-limitation-date-days";
        var daysTypeField = "lecm-ord-table-structure:item-limitation-date-type";
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
