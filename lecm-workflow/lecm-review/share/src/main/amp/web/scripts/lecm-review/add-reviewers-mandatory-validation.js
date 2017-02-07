(function () {
    YAHOO.Bubbling.on('pickerReady', onPickerReady);

    function onPickerReady(layer, args) {
        var obj = args[1];

        YAHOO.Bubbling.fire('registerValidationHandler',
            {
                fieldId: obj.eventGroup.id + '-items',
                handler: LogicECM.module.Documents.reviewerMandatory,
                when: "propertyChange",
                message: Alfresco.util.message("Alfresco.forms.validation.mandatory.message")
            });
    }
})();
