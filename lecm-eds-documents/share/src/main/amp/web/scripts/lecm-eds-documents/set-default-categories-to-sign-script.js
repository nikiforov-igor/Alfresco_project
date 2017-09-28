(function () {
    var Bubbling = YAHOO.Bubbling,
        Dom = YAHOO.util.Dom;

    Bubbling.on('esSignRequiredChanged', onEsSignedChange);

    function onEsSignedChange(layer, args) {
        var formId = args[1].formId;
        var esSignedRequiredEl = Dom.get(formId + "_prop_lecm-doc-dic-dt_es-sign-required");
        var categoriesToSignSelect = Dom.get(formId + "_prop_lecm-doc-dic-dt_categories-of-attachments-to-sign-entry");
        var categoriesToSignHiddenEl = Dom.get(formId + "_prop_lecm-doc-dic-dt_categories-of-attachments-to-sign");
        var options = categoriesToSignSelect.options;

        if (categoriesToSignSelect.options.selectedIndex == -1 && esSignedRequiredEl.value == "true") {
            for (var i = 0, len = options.length; i < len; i++) {
                if (options[i].value == "Все") {
                    options[i].selected = true;
                    categoriesToSignHiddenEl.value = options[i].value;
                    break;
                }
            }
        }
    }
})();