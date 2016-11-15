if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {
    YAHOO.Bubbling.on('pickerReady', onPickerReady);

    function onPickerReady(layer, args) {
        var obj = args[1];

        YAHOO.Bubbling.fire('registerValidationHandler',
            {
                fieldId: obj.eventGroup.id + '-items',
                handler: LogicECM.module.Incoming.reviewerMandatory,
                when: "propertyChange",
                message: Alfresco.util.message("Alfresco.forms.validation.mandatory.message")
            });
    }

    LogicECM.module.Base.Util.loadScripts([
        'scripts/lecm-review/reviewers-mandatory-validator.js'
    ]);
})();
