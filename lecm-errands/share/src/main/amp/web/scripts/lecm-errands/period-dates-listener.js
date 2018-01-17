(function () {
    if (YAHOO.Bubbling.addLayer("periodDatesScriptLoaded")) {
        YAHOO.Bubbling.on("periodDatesScriptLoaded", init);
    }

    function init(layer, args) {
        var params = args[1];
        if (params.bubblingName) {
            YAHOO.Bubbling.on(params.bubblingName, periodEndDateRadioChanged, params);
        }
        if (params.formId && params.startDateField) {
            var startDateFieldReadyEl = LogicECM.module.Base.Util.getComponentReadyElementId(params.formId, params.startDateField);
            YAHOO.util.Event.onContentReady(startDateFieldReadyEl, function () {
                var startDateControl = Dom.get(params.formId + "_prop_" + params.startDateField.replace(":","_") + "-cntrl-parent");
                var mandatoryEl = document.createElement('span');
                mandatoryEl.className = "mandatory-indicator";
                mandatoryEl.innerHTML = "*";
                var startDateLabelDiv = Selector.query(".label-div label", startDateControl, true);
                startDateLabelDiv.appendChild(mandatoryEl);
            });
        }
    }

    function periodEndDateRadioChanged(layer, args, params) {
        var value = args[1].value;
        var formId = args[1].formId;

        if (formId && params.endDateField && params.duringCountField && params.duringTypeField && params.reiterationCountField) {
            if ("REPEAT_COUNT" == value) {
                LogicECM.module.Base.Util.disableControl(formId, params.duringCountField, true);
                LogicECM.module.Base.Util.disableControl(formId, params.duringTypeField, true);
                LogicECM.module.Base.Util.disableControl(formId, params.endDateField, true);
                LogicECM.module.Base.Util.enableControl(formId, params.reiterationCountField, false);
            } else if ("DATERANGE" == value) {
                LogicECM.module.Base.Util.disableControl(formId, params.duringCountField, true);
                LogicECM.module.Base.Util.disableControl(formId, params.duringTypeField, true);
                LogicECM.module.Base.Util.disableControl(formId, params.reiterationCountField, true);
                LogicECM.module.Base.Util.enableControl(formId, params.endDateField, false);
            } else if ("DURING" == value) {
                LogicECM.module.Base.Util.disableControl(formId, params.endDateField, true);
                LogicECM.module.Base.Util.disableControl(formId, params.reiterationCountField, true);
                LogicECM.module.Base.Util.enableControl(formId, params.duringCountField, false);
                LogicECM.module.Base.Util.enableControl(formId, params.duringTypeField, false);
            } else {
                LogicECM.module.Base.Util.disableControl(formId, params.endDateField, true);
                LogicECM.module.Base.Util.disableControl(formId, params.reiterationCountField, true);
                LogicECM.module.Base.Util.disableControl(formId, params.duringTypeField, true);
                LogicECM.module.Base.Util.disableControl(formId, params.duringCountField, true);
            }
        }
    }
})();