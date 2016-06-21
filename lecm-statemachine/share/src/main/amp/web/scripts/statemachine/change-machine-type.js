(function() {

    YAHOO.Bubbling.on('changeMachineType', reInit);

    var PREV_VALUE = null;

    function reInit(layer, args) {
        var formId = args[1].formId;
        var control = args[1].control;

        var currentValue = false;
        if(control && control.checkbox) {
            currentValue = control.checkbox.checked;
        }

        if (currentValue != PREV_VALUE) {
            if (PREV_VALUE != null) {
                var selector = YAHOO.util.Dom.get(formId + "_prop_lecm-stmeditor_archiveFolder");
                if (selector) {
                    selector.value = "";

                    LogicECM.module.Base.Util.reInitializeControl(formId, "lecm-stmeditor:archiveFolder", {
                        defaultValueDataSource: "lecm/statemachine/getDefaultArchivePath?simple=" + currentValue
                    });
                }
            }
            PREV_VALUE = currentValue;
        }
    }

})();