(function() {

    YAHOO.Bubbling.on('errandControllerChanged', enableReportRecipientSelectOptions);
    YAHOO.Bubbling.on('errandReportRequiredChanged', toggleReportRecipientField);

    YAHOO.Bubbling.on('createErrandsWFErrandControllerChanged', enableReportRecipientSelectOptions);
    YAHOO.Bubbling.on('createErrandsWFErrandReportRequiredChanged', toggleReportRecipientField);

    function enableReportRecipientSelectOptions(layer, args) {
        var propName = "lecm-errands_report-recipient-type";
        if (layer == "createErrandsWFErrandControllerChanged") {
            propName = "lecmErrandWf_reportRecipientType";
        }
        var recipientField = Dom.get(args[1].formId + '_prop_' + propName);
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

    function toggleReportRecipientField(layer, args) {
        var propName = "lecm-errands_report-recipient-type";
        if (layer == "createErrandsWFErrandReportRequiredChanged") {
            propName = "lecmErrandWf_reportRecipientType";
        }
        var requiredReportField = Dom.get(args[1].formId + '_prop_' + args[1].fieldId.replace('\:', '_'));
        var recipientField = Dom.get(args[1].formId + '_prop_' + propName);
        if (requiredReportField && recipientField) {
            var requiredReport = requiredReportField.value == "true";
            Dom.setStyle(recipientField.parentNode.parentNode.parentNode, "display", !requiredReport ? "none" : "block");
        }
    }
})();