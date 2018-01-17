(function() {
    YAHOO.Bubbling.on('errandsPeriodicalHandlerScriptLoaded', init);
    YAHOO.Bubbling.on('errandPeriodicallyPropChanged', manageVisibility);

    var formID, fieldID;
    function init(layer, args) {
        formID = args[1].formId;
        fieldID = args[1].periodicallyField;
    }

    function manageVisibility(layer, args) {
        if (formID && fieldID) {
            var field = YAHOO.util.Dom.get(formID + "_prop_" + fieldID.replace(":","_"));
            var periodicallySet = YAHOO.util.Selector.query(".set > .periodicallySet", YAHOO.util.Dom.get(formID), true);
            if (field && field.value == "true") {
                if (periodicallySet) {
                    YAHOO.util.Dom.removeClass(periodicallySet, "hidden1");
                }
            } else {
                YAHOO.util.Dom.addClass(periodicallySet, "hidden1");
            }
        }
    }

})();