(function()
{
    YAHOO.Bubbling.on("changeProviderType", changeProviderType);

    function changeProviderType(layer, args) {
        var selectProviderName = null;
        var formId = args[1].formId;
        var fieldId = args[1].fieldId;

        var selector = YAHOO.util.Dom.get(formId + "_" + fieldId);
        if (selector !== null) {
            selectProviderName = selector[selector.selectedIndex].text;
        }

        if (formId != null) {
            if (selectProviderName == "SQLProvider") {
                LogicECM.module.Base.Util.enableControl(formId, "lecm-rpeditor:loadColumnsFromSQL");
            } else {
                LogicECM.module.Base.Util.disableControl(formId, "lecm-rpeditor:loadColumnsFromSQL");
            }
        }
    }
})();