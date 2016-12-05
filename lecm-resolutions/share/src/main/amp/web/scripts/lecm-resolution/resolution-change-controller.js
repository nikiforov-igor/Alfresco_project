(function() {
    YAHOO.Bubbling.on('resolutionControllerChanged', onResolutionControllerChange);

    function onResolutionControllerChange(layer, args) {
        var recipientField = Dom.get(args[1].formId + '_prop_lecm-resolutions_closers');
        if (recipientField && recipientField.tagName == "SELECT") {
            if (args[1].selectedItems && Object.keys(args[1].selectedItems).length) {
                recipientField.options[1].disabled = false;
                recipientField.options[2].disabled = false;
            } else {
                recipientField.options[1].disabled = true;
                recipientField.options[2].disabled = true;
                recipientField.selectedIndex = 0;
            }
        }
    }
})();