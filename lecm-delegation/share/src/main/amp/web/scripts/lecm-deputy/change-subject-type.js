(function () {
	YAHOO.Bubbling.on("formValueChanged", onFormValueChanged);

	function onFormValueChanged(layer, args) {
		var obj = args[1];
		if (obj.eventGroup.options.fieldId == 'lecm-deputy:subject-assoc') {
			YAHOO.Bubbling.unsubscribe("formValueChanged", onFormValueChanged);
			if (LogicECM.module.Deputy.Const.path && LogicECM.module.Deputy.Const.itemType) {
				LogicECM.module.Base.Util.reInitializeControl(obj.eventGroup.options.formId, obj.eventGroup.options.fieldId, {
					rootLocation: LogicECM.module.Deputy.Const.path,
					startLocation: LogicECM.module.Deputy.Const.path,
					itemType: LogicECM.module.Deputy.Const.itemType
				});
			}
		}
	}
})();
