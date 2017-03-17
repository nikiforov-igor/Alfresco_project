(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;

    if (Bubbling.addLayer("ordControllerChangedEvent")) {
        Bubbling.on('ordControllerChangedEvent', processField);
    }

    function processField(layer, args) {
        var formId = args[1].formId;
        var selectedItems = args[1].selectedItems;
        var confirmCompletionValueEl = Dom.get(formId + "_prop_" + "lecm-ord_confirm-completion");
        var confirmCompletionCheckBox = Dom.get(formId + "_prop_" + "lecm-ord_confirm-completion-entry");
        var confirmCompletionDiv = confirmCompletionValueEl.parentElement.parentElement.parentElement;
        Dom.setStyle(confirmCompletionDiv, "display", !selectedItems || !Object.keys(selectedItems).length ? "none" : "block");
        if (!selectedItems || !Object.keys(selectedItems).length) {
            if (confirmCompletionValueEl.value == "true" && confirmCompletionCheckBox) {
                confirmCompletionCheckBox.click();
            }
        }
    }
})();