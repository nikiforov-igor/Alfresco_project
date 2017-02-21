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
        var confirmCompletionDiv = Dom.get(formId + "_prop_" + "lecm-ord_confirm-completion").parentElement.parentElement.parentElement;
        Dom.setStyle(confirmCompletionDiv, "display", !selectedItems || !Object.keys(selectedItems).length ? "none" : "block")
    }
})();