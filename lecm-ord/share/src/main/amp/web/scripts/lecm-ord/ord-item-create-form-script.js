(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling,
        Util = LogicECM.module.Base.Util;

    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-ord/ord-date-set-with-icon.css'
    ]);

    if (Bubbling.addLayer("ordItemReportRequiredChanged")) {
        Bubbling.on('ordItemReportRequiredChanged', processController);
    }

    function processController(layer, args) {
        var formId = args[1].formId;
        var fieldHtmlId = formId + '_prop_' + args[1].fieldId.replace('\:', '_');
        var value = Dom.get(fieldHtmlId).value == "true";
        if (value) {
            Util.readonlyControl(formId, "lecm-ord-table-structure:controller-assoc", false);
        } else {
            var controllerField = Dom.get(formId + "_assoc_lecm-ord-table-structure_controller-assoc");
            if (controllerField && controllerField.value) {
                var controllerControl = Dom.get(formId + "_assoc_lecm-ord-table-structure_controller-assoc-cntrl");
                Selector.query(".value-div a.remove-item", controllerControl, true).click();
            }
            Util.readonlyControl(formId, "lecm-ord-table-structure:controller-assoc", true);
        }
    }
})();