(function() {

    YAHOO.Bubbling.on('errandControllerChanged', enableReportRecipientSelectOptions);

    function enableReportRecipientSelectOptions(layer, args) {
        var recipientField = Dom.get(args[1].formId + '_prop_lecm-errands_report-recipient-type');
        if (recipientField && recipientField.tagName == "SELECT") {
            if (args[1].selectedItems && Object.keys(args[1].selectedItems).length != 0) {
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