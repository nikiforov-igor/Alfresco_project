(function () {
    if (YAHOO.Bubbling.addLayer("complexDateScriptLoaded")) {
        YAHOO.Bubbling.on("complexDateScriptLoaded", init);
    }

    function init(layer, args) {
        var params = args[1];
        if (params.bubblingName) {
            YAHOO.Bubbling.on(params.bubblingName, complexDateRadioChanged, params);
        }
    }

    function complexDateRadioChanged(layer, args, params) {
        var value = args[1].value;
        var formId = args[1].formId;

        if (formId && params.dateField && params.daysCountField && params.daysTypeField) {
            if ("DAYS" == value) {
                LogicECM.module.Base.Util.enableControl(formId, params.daysCountField, false);
                LogicECM.module.Base.Util.enableControl(formId, params.daysTypeField, false);
                LogicECM.module.Base.Util.disableControl(formId, params.dateField, true);
            } else if ("DATE" == value) {
                LogicECM.module.Base.Util.disableControl(formId, params.daysCountField, true);
                LogicECM.module.Base.Util.disableControl(formId, params.daysTypeField, true);
                LogicECM.module.Base.Util.enableControl(formId, params.dateField, false);
            } else {
                LogicECM.module.Base.Util.disableControl(formId, params.daysCountField, true);
                LogicECM.module.Base.Util.disableControl(formId, params.daysTypeField, true);
                LogicECM.module.Base.Util.disableControl(formId, params.dateField, true);
            }
        }
    }
})();