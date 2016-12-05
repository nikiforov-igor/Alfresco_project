(function ()
{
    YAHOO.Bubbling.on("changeParamType", onChangeParamType);


    function onChangeParamType(layer, args) {
        var formId = args[1].formId;
        if (formId !== null) {
            var selectedItems = args[1].selectedItems;
            if (selectedItems !== null) {
                var keys = Object.keys(selectedItems);
				LogicECM.module.Base.Util.readonlyControl(formId, "lecm-rpeditor:dataColumnClass", !keys || !keys.length);
				LogicECM.module.Base.Util.readonlyControl(formId, "lecm-rpeditor:dataColumnOrder", !keys || !keys.length);
				LogicECM.module.Base.Util.readonlyControl(formId, "lecm-rpeditor:dataColumnMandatory", !keys || !keys.length);
				LogicECM.module.Base.Util.readonlyControl(formId, "lecm-rpeditor:dataColumnControlParams", !keys || !keys.length);
            } else {
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-rpeditor:dataColumnClass", true);
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-rpeditor:dataColumnOrder", true);
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-rpeditor:dataColumnMandatory", true);
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-rpeditor:dataColumnControlParams", true);
            }
        }
    }

})();
