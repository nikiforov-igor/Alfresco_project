(function ()
{
    YAHOO.Bubbling.on("changeParamType", onChangeParamType);


    function onChangeParamType(layer, args) {
        var formId = args[1].formId;
        if (formId !== null) {
            var selectedItems = args[1].selectedItems;
            if (selectedItems !== null) {
                var keys = Object.keys(selectedItems);
                if (keys !== null && keys.length > 0) {
                    LogicECM.module.Base.Util.enableControl(formId, "lecm-rpeditor:dataColumnClass");
                    LogicECM.module.Base.Util.enableControl(formId, "lecm-rpeditor:dataColumnOrder");
                    LogicECM.module.Base.Util.enableControl(formId, "lecm-rpeditor:dataColumnMandatory");
                    LogicECM.module.Base.Util.enableControl(formId, "lecm-rpeditor:dataColumnControlParams");
                } else {
                    LogicECM.module.Base.Util.disableControl(formId, "lecm-rpeditor:dataColumnClass");
                    LogicECM.module.Base.Util.disableControl(formId, "lecm-rpeditor:dataColumnOrder");
                    LogicECM.module.Base.Util.disableControl(formId, "lecm-rpeditor:dataColumnMandatory");
                    LogicECM.module.Base.Util.disableControl(formId, "lecm-rpeditor:dataColumnControlParams");
                }
            } else {
                LogicECM.module.Base.Util.disableControl(formId, "lecm-rpeditor:dataColumnClass");
                LogicECM.module.Base.Util.disableControl(formId, "lecm-rpeditor:dataColumnOrder");
                LogicECM.module.Base.Util.disableControl(formId, "lecm-rpeditor:dataColumnMandatory");
                LogicECM.module.Base.Util.disableControl(formId, "lecm-rpeditor:dataColumnControlParams");
            }
        }
    }

})();