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
			LogicECM.module.Base.Util.readonlyControl(formId, 'lecm-rpeditor:loadColumnsFromSQL', selectProviderName != "SQLProvider");
        }
    }
})();
