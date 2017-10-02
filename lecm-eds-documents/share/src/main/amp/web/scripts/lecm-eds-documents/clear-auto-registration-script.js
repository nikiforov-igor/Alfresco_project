(function () {
    var Bubbling = YAHOO.Bubbling,
        Dom = YAHOO.util.Dom;

    Bubbling.on('registrationRequiredChange', onRegistrationRequiredChange);

    function onRegistrationRequiredChange(layer, args) {
        var formId = args[1].formId;
        var registrationRequiredEl = Dom.get(formId + "_prop_lecm-doc-dic-dt_registration-required-entry");
        var autoRegistrationEl = Dom.get(formId + "_prop_lecm-doc-dic-dt_auto-registration-entry");
        var autoRegistrationHiddenEl = Dom.get(formId + "_prop_lecm-doc-dic-dt_auto-registration");

        if (registrationRequiredEl.checked == false) {
            autoRegistrationEl.checked = false;
            autoRegistrationHiddenEl.value = false;
            autoRegistrationEl.disabled = true;
        } else {
            autoRegistrationEl.disabled = false;
        }
    }
})();