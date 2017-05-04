(function()
{
    YAHOO.Bubbling.on("changeProviderType", changeProviderType);

    function changeProviderType(layer, args) {
        var selectProviderName = null;
        var formId = args[1].formId;
        var selectedItems = args[1].selectedItems;
        if (selectedItems) {
            var keys = Object.keys(selectedItems);
            if (keys && keys.length){
                selectProviderName = selectedItems[keys[0]]["selectedName"];
            }
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