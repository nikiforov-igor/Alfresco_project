(function() {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-view-form.css'
    ]);
    var Dom = YAHOO.util.Dom,
        Bubbling = YAHOO.Bubbling;

    Bubbling.on("errandsTabsFormScriptLoaded", processForm);

    function processForm(layer, args){
        var formId = args[1].formId;
        if (formId) {
            var periodicallyFieldId = "lecm-errands:periodically";
            var field = YAHOO.util.Dom.get(formId + "_prop_" + periodicallyFieldId.replace(":", "_"));
            var periodicallySet = YAHOO.util.Selector.query(".set > .periodicallySet", YAHOO.util.Dom.get(formId), true);
            if (field && field.value == "true" && periodicallySet) {
                    YAHOO.util.Dom.removeClass(periodicallySet, "hidden1");
            } else {
                YAHOO.util.Dom.addClass(periodicallySet, "hidden1");
            }

            var limitationDateControlField = "lecm-errands:limitation-date-cntrl-date";
            var limitationDateRadioField = "lecm-errands:limitation-date-radio";
            var limitationDateControl = Dom.get(formId + "_prop_" + limitationDateControlField.replace(":", "_"));
            if(limitationDateControl.disabled){
                LogicECM.module.Base.Util.readonlyControl(formId, limitationDateRadioField, true);
            }
        }
    }

})();