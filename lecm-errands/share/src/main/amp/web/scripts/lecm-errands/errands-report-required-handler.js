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
            if(propName == "lecm-errands_report-recipient-type") {
                if (!requiredReport) {
                    showOption(recipientField);
                    Dom.get(args[1].formId + '_prop_' + propName).selectedIndex = 3;
                } else {
                    hideOption(recipientField.options[3]);
                    if (Dom.get(args[1].formId + '_prop_' + propName).selectedIndex == 3) {
                        Dom.get(args[1].formId + '_prop_' + propName).selectedIndex = 0;
                    }
                }
            }
        }
    }

    /**
     * IE 11 is not support hidden or display:none option in select. This function allow hide option in select.
     * Option will be replaced on span with id = select.id + "_hidden_opt".
     *
     * @param opt html element option in select for hide
     */
    function hideOption(opt) {
        var select = opt.parentElement;
        var span = document.createElement("span");
        span.hidden = true;
        span.title = opt.value;
        span.textContent = opt.textContent;
        span.id = select.id + "_hidden_opt";
        select.appendChild(span);
        select.removeChild(opt);
    }

    /**
     * IE 11 is not support hidden or display:none option in select. This function show hidden option.
     * Get hidden option with id select.id + "_hidden_opt" and add it to current select
     *
     * @param select html element select for option
     */
    function showOption(select) {
        var hiddenOpt = Dom.get(select.id + "_hidden_opt");
        if (hiddenOpt) {
            var option = document.createElement("option");
            option.value = hiddenOpt.title;
            option.textContent = hiddenOpt.textContent;
            select.appendChild(option);
            select.removeChild(hiddenOpt);
        }
    }
})();